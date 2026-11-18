# Brightline Loyalty Points Service

A loyalty points service for Brightline, refactored from AI-generated starter code to the
AmaliTech Coding Standards, with a test suite that would catch a real bug.

## Documents

| File | Contents |
|---|---|
| [ANALYSIS.md](ANALYSIS.md) | What was wrong with the inherited code and tests, written before any change, with the most serious problem ranked first |
| [RATIONALE.md](RATIONALE.md) | Why each refactoring decision was made, how the new tier fits in, what was kept, fixed or rewritten, and the improvement beyond the brief |
| [AI-USAGE.md](AI-USAGE.md) | Which AI tools were used and for what, what was verified and how, and what was rejected or corrected |
| [before-refactor.txt](before-refactor.txt) | `mvn test` on the untouched starter code: 3 tests |
| [after-refactor.txt](after-refactor.txt) | `mvn test` on the refactored code: 59 tests |

## Points rules

- One point for every full GHS 10 spent.
- STANDARD keeps the base points, SILVER multiplies them by 1.5 (rounded down), GOLD by 2.
- Purchases above GHS 500 earn a 50 point bonus.
- An unknown member, or an amount that is null, negative, above GHS 1,000,000 or has more than
  two decimal places, is rejected with a reason.

## Running it

Requires JDK 21 and Maven.

```bash
mvn test                                                     # run the test suite
mvn compile exec:java -Dexec.mainClass=com.brightline.loyalty.Demo   # run the demo
mvn verify                                                   # tests + JaCoCo report: target/site/jacoco/index.html
mvn test-compile org.pitest:pitest-maven:mutationCoverage    # PIT report: target/pit-reports/index.html
```

If the exec plugin is unavailable, run the demo from the compiled classes after `mvn compile`:

```bash
java -cp target/classes com.brightline.loyalty.Demo
```

## Project layout

```
src/main/java/com/brightline/loyalty/
├── Demo.java                          wires the service and runs sample purchases
├── exception/
│   ├── LoyaltyException.java          unchecked base for all loyalty failures
│   ├── InvalidPurchaseException.java
│   ├── MemberNotFoundException.java
│   └── UnsupportedTierException.java
├── model/
│   ├── Member.java                    record, validated on construction
│   └── Tier.java                      STANDARD, SILVER, GOLD
├── repository/
│   ├── MemberRepository.java          findById returns Optional<Member>
│   └── InMemoryMemberRepository.java
├── rule/
│   ├── bonus/
│   │   └── SpendBonusRule.java        50 points above GHS 500
│   └── tier/
│       ├── TierRule.java              one implementation per tier
│       ├── StandardTierRule.java
│       ├── SilverTierRule.java
│       ├── GoldTierRule.java
│       └── TierRuleRegistry.java      maps each Tier to its rule
└── service/
    └── PointsCalculator.java          validate, find member, apply tier rule, add bonus

src/test/java/com/brightline/loyalty/
├── model/MemberTest.java
├── repository/InMemoryMemberRepositoryTest.java
├── rule/bonus/SpendBonusRuleTest.java
├── rule/tier/
│   ├── StandardTierRuleTest.java
│   ├── SilverTierRuleTest.java
│   ├── GoldTierRuleTest.java
│   └── TierRuleRegistryTest.java
└── service/PointsCalculatorTest.java
```

## Adding a tier

1. Add the tier to `Tier`.
2. Add a class that implements `TierRule`.
3. Register it where the `TierRuleRegistry` is built (in `Demo`).
4. Add a test for the new rule.

No existing class changes. See [RATIONALE.md](RATIONALE.md) for the reasoning.
