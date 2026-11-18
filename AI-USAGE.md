# AI Usage Statement

**Name:** Kephas Tetteh
**Lab / deliverable:** Code Quality & Testing applied lab, Brightline Loyalty points service
**Date:** 18 November 2026

## 1. Tools

Kiro (AI coding assistant in the Kiro IDE), with the model selected automatically by Kiro. I used
it as a pair programmer that could read the repository, run Maven, and edit files only after I
accepted each change.

## 2. What I used it for

- **Analysis:** I did the analysis of the code and tests and identified the smells, SOLID
  violations and hollow tests. Kiro cross-checked my findings against the AmaliTech standard's
  section numbers and helped me write them up as `ANALYSIS.md`.
- **Refactoring:** it wrote the code for each step I decided on: constructor injection, the
  `Tier` enum and `Member` record, the exception hierarchy, the `TierRule` classes and registry,
  the `SpendBonusRule` extraction, the `BigDecimal` change, input validation and logging.
- **Tests:** it wrote the JUnit 5 and Mockito suite, and ran PIT to measure it.
- **Documents and commits:** it drafted `RATIONALE.md`, the README and the commit messages, which
  I reviewed and edited before each commit.
- **Learning:** it taught me the Strategy pattern and how a registry selects the right rule
  without an if/else chain, and fail-fast validation with `Objects.requireNonNull` and record
  compact constructors.

## 3. What I verified, and how

- **Behaviour:** I ran `mvn test` after every change and before every commit. The final run is 59
  tests, 0 failures (`after-refactor.txt`, against 3 tests in `before-refactor.txt`).
- **Points stayed the same:** I ran the demo after each refactoring step and checked that m1 GHS 100,
  m2 GHS 250 and m3 GHS 200 still gave 10, 50 and 30 points, the same as the starter code.
- **The tests catch bugs:** PIT killed 30 of 30 mutants in the business code, including `>`
  changed to `>=` on the bonus. All 10 uncovered mutants are in `Demo`, which only prints.
- **Every commit builds:** where one change was split into two commits, the first was built in a
  separate checkout before the second was made.
- **Docs match the code:** I checked the README's run command by running it, its layout tree
  against the 24 source files, and the line-length rule (100 characters) with a scan.

## 4. What I rejected or corrected

- **Method name.** It named the tier rule method `applyTo`. I rejected that because it does not
  say what the method returns (§3.1), and renamed it `calculateTierPoints`.
- **Removing the exception getters.** It started deleting `getMemberId()` and `getAmount()` as
  unused. I stopped that because the tests use them to check which value failed, without depending
  on message wording.
- **Leaving `double` for money.** It suggested the `double` amounts and `int` cast could stay and be
  listed as follow-up work. I made it fix them, because my analysis had flagged this as problem 8.
  That became `BigDecimal`, the GHS 1,000,000 cap and the decimal-places check.
- **One flat package.** Its first version kept every class in one package. I had it split into
  `model`, `repository`, `service`, `exception` and `rule` before committing.
- **Noise comments.** It added a Javadoc block to `LoyaltyException` that only restated the class
  name. I removed it (§10.1).
- **Scope I chose not to take.** It proposed a characterization test containing a copy of the
  original formula, and a startup check in the registry. I rejected both to keep the change focused,
  and kept the existing exact-value tests as the evidence for unchanged behaviour.
- **Overstated claims.** Its first drafts said coverage "would look high" on the bonus line (the
  line was actually uncovered) and that the demo matched "for every valid purchase" (only three
  purchases were compared). Both were corrected to say exactly what was checked.

## 5. Ownership

I can explain and defend every part of this submission, including the design choices I rejected
and why.
