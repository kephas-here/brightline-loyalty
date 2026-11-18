# Rationale for the Brightline Loyalty refactor

## Result in numbers

| | Before (`before-refactor.txt`) | After (`after-refactor.txt`) |
|---|---|---|
| Test classes | 1 | 8 |
| Tests | 3 | 59 |
| Tests that could fail if the code were wrong | 1, and only weakly | 59 |
| PIT mutants killed in business code | not measured | 30 of 30 |

The scheme's rules did not change: one point per full GHS 10, SILVER ×1.5 rounded down, GOLD ×2,
and a 50 point bonus above GHS 500. The demo's purchases (m1 GHS 100, m2 GHS 250, m3 GHS 200) give
the same points as before the refactor. What changed is failure handling: a missing or unknown
member, or an amount that is null, negative, above GHS 1,000,000 or has more than two decimal
places, is now rejected with a reason instead of returning 0 or being accepted.

## Key decisions

### 1. Stop swallowing failures first

The `catch (Exception e) { return 0; }` was the most serious problem, so it was the first
behaviour I fixed. A missing member, a bug and a bad amount all looked like a valid award of zero.

- The repository returns `Optional<Member>` instead of `null`, so "not found" has to be handled.
- Failures are custom exceptions under one unchecked base, `LoyaltyException`:
  `MemberNotFoundException`, `InvalidPurchaseException` and `UnsupportedTierException`. Each one
  carries the value that failed (member id, amount or tier), so callers and tests can check it
  without parsing the message.
- I made them unchecked because they come from bad input, which code in the middle cannot fix.
  Checked exceptions would have forced `throws` clauses or catch-and-rethrow blocks through every
  layer, and that pattern is how the original ended up swallowing errors. They travel to `Demo`,
  which is the level that can act on them (§6.1).

### 2. Inject every dependency

`PointsCalculator` built its own `InMemoryMemberRepository`, so no test could replace it. The
repository, the tier rules and the bonus rule now come in through the constructor (§12.5). This
is what made the failure paths testable with Mockito.

The constructor takes three collaborators. §4.3 asks for few arguments, but these are the class's
dependencies, not inputs to a calculation, and grouping them into one object would hide what the
class depends on.

### 3. One class per tier rule, chosen through a registry

The tier multipliers were an if/else chain on Strings. I replaced it with the Strategy pattern:

- `TierRule` is an interface with `calculateTierPoints(int basePoints)`.
- `StandardTierRule`, `SilverTierRule` and `GoldTierRule` each implement it.
- `TierRuleRegistry` holds an `EnumMap` from `Tier` to its rule. The calculator asks it for the
  rule for the member's tier. There is no if/else chain, and a tier with no rule throws
  `UnsupportedTierException` instead of silently getting no multiplier.

`Tier` became an enum and `Member` a record, so an invalid tier is a compile error rather than a
typo that compiles.

### 4. Split the calculation into small named steps

`calculatePoints` now reads as the business process: validate the amount, find the member, apply
the tier rule to the base points, add the bonus. Every magic number is a named constant
(`SPEND_PER_POINT`, `BONUS_THRESHOLD`, `BONUS_POINTS`, the multipliers), the `println` side effect
is gone (§4.4), and the comments that restated the code are deleted (§10.1).

### 5. Organise by layer

Code is split into `model`, `repository`, `service`, `exception`, `rule.tier` and `rule.bonus`.
Tests mirror the same packages.

### 6. Reject invalid objects when they are built

An invalid object should fail where it is created, not crash somewhere later with a
`NullPointerException` that points at the wrong line.

- `Member` checks its fields in the record's compact constructor: the id and name must not be null
  or blank, and the tier must not be null. No invalid `Member` can exist.
- `PointsCalculator` and `TierRuleRegistry` reject null collaborators and null rules with
  `Objects.requireNonNull` and a message naming what is missing.
- A null or blank member id is rejected with `MemberNotFoundException` before the repository is
  called.

Null and blank checks on objects being built are programming errors, so they use the JDK's
`NullPointerException` and `IllegalArgumentException`. Bad input for a purchase is a business
failure, so it uses the `LoyaltyException` types that `Demo` handles.

### 7. Make failures observable at the boundary

`Demo` logs every rejected purchase at `WARNING` with the member, amount and reason, using
`java.util.logging` so there is no new dependency. Any other runtime failure, such as the
repository being unavailable, is caught at this top level and logged at `SEVERE` with its stack
trace, so one bad purchase does not crash the run. Logging stays at the program boundary: the
calculator has no side effects (§4.4).

## How the design absorbs the new tier

Adding a tier, for example PLATINUM at ×3:

1. Add `PLATINUM` to the `Tier` enum.
2. Add `PlatinumTierRule implements TierRule`.
3. Add `Tier.PLATINUM, new PlatinumTierRule()` where the registry is built.
4. Add a `PlatinumTierRuleTest`.

`PointsCalculator`, `TierRuleRegistry` and the existing rules do not change (§12.2). If step 3 is
forgotten, the first PLATINUM purchase fails with `UnsupportedTierException` naming the tier,
instead of quietly awarding the wrong points.

## Alternatives I considered

| Option | Why I did not choose it |
|---|---|
| Abstract `Tier` parent class with subclasses | The tiers share no state or behaviour, so the parent would only hold an abstract method. An interface does the same with less coupling, and §2.2 prefers composition |
| A `Member` subclass per tier (`GoldMember` and so on) | A member's tier changes over time, but an object's class cannot. The if/else would also move into the repository to decide which subclass to build, and `Member` would mix data with points rules |
| The rule as a method on each `Tier` enum constant | Simplest, and a missing rule would not compile. I chose separate classes because each rule can be tested and changed on its own, and a more complex future tier would crowd the enum file |
| A `TierRule` interface for the bonus too | Only one bonus exists. A single injected `SpendBonusRule` class is enough until a second bonus appears (YAGNI) |

## What I kept, fixed and rewrote

| Part | Decision | Why |
|---|---|---|
| Points rules (rates, bonus, rounding down) | Kept | They are the business behaviour. The tests now pin them down with exact values |
| `MemberRepository` interface | Kept, fixed | The abstraction was right. It returned `null`, so it now returns `Optional<Member>` |
| `InMemoryMemberRepository` and its seed data | Kept | Useful for the demo and its own tests. Seeding now uses `Tier` values |
| `Member` | Fixed | String tier became an enum, the getter-only class became a record, and it rejects missing fields |
| `Demo` | Fixed | It now wires the dependencies, and reports and logs rejections instead of printing 0 |
| `PointsCalculator` | Rewritten inside | Same public purpose. Its jobs were moved out into rules, and it no longer swallows errors |
| Tier if/else chain | Rewritten | Replaced by `TierRule` classes and the registry |
| `calculatesGoldPoints` test | Deleted | It mocked the class under test, so no real code ran and it could never fail |
| `awardsPointsForAPurchase` test | Rewritten | `assertNotNull` on an `int` always passes. Now asserts SILVER 200 gives exactly 30 |
| `awardsPointsToAMember` test | Rewritten | `points > 0` passed for any positive number. Now asserts STANDARD 100 gives exactly 10 |

## The test suite

- Only `MemberRepository` is mocked. The calculator, tier rules and bonus rule run for real, so
  the tests check behaviour, not that a mock was called.
- Every test asserts an exact value or a specific exception type, and checks the value the
  exception carries.
- Cases cover each tier, SILVER rounding, amounts below one point, the bonus boundary (500 gets no
  bonus, 500.01 does), the amount cap, each kind of rejected amount, unknown members, a repository
  failure passing through unchanged, and a tier with no rule.
- The invalid-amount and missing-member-id tests also verify the repository is never called, so
  validation happens before any lookup.
- `Member`, the calculator and the registry each have tests for every null or blank input they
  reject.
- Parameterized tests keep each case to one line of data instead of copied test methods.

PIT mutation testing killed all 30 mutants in the business code, including `>` changed to `>=`
on the bonus, which the original tests would have let through. The 10 mutants with no coverage
are all in `Demo`, the console runner, which only prints and logs and has no business logic.

## Improvement beyond the brief: a money guard

The analysis flagged `double` for money (problem 8). It was not one of the brief's requirements,
but I fixed it because it is a whole class of bug in code that awards value to customers.

- **Precision.** A `double` cannot hold most decimal amounts exactly, so a GHS 70 purchase that
  arrives as `69.99999999` would lose a point. Amounts are now `BigDecimal`, and base points use
  `divide` with an explicit `RoundingMode.DOWN`.
- **Overflow.** A very large amount overflowed the `int` cast, and the GOLD multiplier could then
  turn it into negative points. Amounts above GHS 1,000,000 are now rejected, and the conversion
  uses `intValueExact`, which throws instead of wrapping.
- **Malformed amounts.** `null`, negative amounts and amounts with more than two decimal places
  are rejected with a reason in the exception message.

Each rule has tests, including 69.99 → 6 and 70.00 → 7 points, and the cap accepted at exactly
1,000,000 and rejected at 1,000,000.01.

Purchases above GHS 1,000,000 are rejected. The brief sets no maximum, so I chose a limit well
above any single shop purchase that keeps every possible award far below the `int` limit (the
largest is 200,050 points). It is one named constant, `MAX_PURCHASE_AMOUNT`, so Brightline can
set its own figure without touching any logic.
