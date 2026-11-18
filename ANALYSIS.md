# Analysis of the inherited Brightline Loyalty code

## Baseline test run

I ran `mvn test` on the untouched starter code and saved the output in `before-refactor.txt`.

- 3 tests run, 0 failures, 0 errors, 0 skipped. `BUILD SUCCESS`.
- The only output from the code itself is `Awarded 10 points to Kofi Mensah` and
  `Awarded 30 points to Yaa Boateng`, which come from the `println` in problem 6. The mocked gold
  test prints nothing, because no real code runs in it (T1).

A fully green run is what made the code look finished. The sections below show why it proves very
little.

## The most serious problem

**`catch (Exception e) { return 0; }` in `PointsCalculator` (lines 32-35).**

When a member does not exist, `repository.findById` returns `null` and line 17 throws a
`NullPointerException`. The catch block turns that into "0 points". A missing member, a bug in the
calculation and an unknown tier all come back as a normal answer of zero. Nothing is reported or
logged, and the Demo prints `0` for an unknown member as if that were correct.

I rank this first because it hides real failures in code that awards value to customers, and
because the existing tests cannot detect it. The code and its safety net are wrong in the same
way. It breaks §6.1 and §6.3.

## Problems in the code

| # | Problem | Where | Why it is bad | How I will fix it |
|---|---|---|---|---|
| 1 | Swallowed exception | `PointsCalculator` 32-35 | Failures look like valid results (§6.1, §6.3) | Add `MemberNotFoundException` and `InvalidPurchaseException`. Remove the catch-all so failures reach the caller |
| 2 | Hard-wired dependency | `PointsCalculator` 7 | The repository cannot be replaced, so failure paths cannot be tested. Violates DIP (§12.5) | Inject `MemberRepository` through the constructor |
| 3 | Tier rules in an if/else chain on Strings | `PointsCalculator` 17-23 | A new tier means editing this method (OCP, §12.2). An unknown tier silently gets no multiplier. `points * 1` does nothing | A `TierRule` interface with one class per tier, and a registry that maps each `Tier` to its rule |
| 4 | One method does everything | `PointsCalculator` 9-36 | Lookup, base points, multiplier, bonus, printing and error handling in 28 lines (SRP §12.1, §4.1, §4.2) | Split into a base-points step, the tier rule, a bonus policy and a small orchestrating method |
| 5 | Magic numbers | 10, 1.5, 2, 500, 50 | Unsearchable, and they hide the business rules (§3.4) | Named constants, with each multiplier inside its tier rule |
| 6 | Side effect in a calculation | `PointsCalculator` 30 | `System.out.println` is hidden behaviour and hard to test (§4.4) | Remove it. Log at the caller if an audit trail is needed |
| 7 | `null` returned for "not found" | `InMemoryMemberRepository` 18 | Nothing forces the caller to handle it. This is where problem 1 starts | Return `Optional<Member>` |
| 8 | `double` for money and a truncating `(int)` cast | `PointsCalculator` 14 | Rounding errors, silent truncation, and no check for negative or NaN amounts | Validate the input and use `BigDecimal` with an explicit rounding mode |
| 9 | Tier is a free-form String | `Member` 6 | The valid values exist only in a comment, and typos compile | `Tier` enum, and make `Member` a record (§8) |
| 10 | Comments that restate the code | `PointsCalculator` 5-6, 13, 16, 25, 33 | Noise. The one on line 33 excuses the bad error handling (§10.1) | Delete them |
| 11 | Minor style | `Demo` teaching comment, one-line getters in `Member`, wildcard imports in the test | §7, §11.1 | Clean up in the final pass |

## Tests that do not test anything

The three existing tests are green but prove almost nothing (§9).

| # | Test | Why it proves nothing | How I will fix it |
|---|---|---|---|
| T1 | `calculatesGoldPoints` | It mocks `PointsCalculator` itself, stubs the answer, then asserts the stub returns it. No real code runs and it can never fail. This is the worst test | Delete it. Test the real calculator with a mocked `MemberRepository` and assert GOLD 250 gives 50 |
| T2 | `awardsPointsForAPurchase` | `assertNotNull` on an `int`. The value is boxed and can never be null, so the assertion always passes | Assert the exact value: SILVER 200 gives 30 |
| T3 | `awardsPointsToAMember` | It only asserts `points > 0`, which passes for 10, 20 or 1000, so most multiplier and bonus bugs go unnoticed. It also relies on the hard-coded repository | Assert the exact value: STANDARD 100 gives 10 |
| T4 | T2 and T3 together | The same test with a different member ID | One parameterized test of tier and amount against expected points |
| T5 | Missing cases | The tests only use amounts of 100 and 200, so the 500 bonus rule on line 26 is never tested. If someone changed `amount > 500` to `amount >= 500`, every test would still pass, and so would a change that removed the bonus altogether | Add tests for the cases listed below, especially an amount of exactly 500 |
| T6 | No mocking possible | The hard-wired repository blocks Mockito | Mock `MemberRepository` once it is injected, and assert the returned value and the exception type |
| T7 | Wildcard imports | §11.1 | Import each name |

The new suite must cover:

- **Happy path:** each tier at a typical amount.
- **Boundaries:** 9.99 (no base points), 10, 500 (no bonus), 500.01 (bonus).
- **Rounding:** SILVER on an odd base, for example 10 gives 15.
- **Failures:** unknown member, negative amount, NaN, and a repository failure propagating.
- **Extensibility:** a new tier works without changing the existing rules.
