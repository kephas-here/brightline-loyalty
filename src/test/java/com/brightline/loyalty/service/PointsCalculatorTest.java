package com.brightline.loyalty.service;

import com.brightline.loyalty.config.LoyaltyConfiguration;
import com.brightline.loyalty.exception.InvalidPurchaseException;
import com.brightline.loyalty.exception.MemberNotFoundException;
import com.brightline.loyalty.exception.UnsupportedTierException;
import com.brightline.loyalty.model.Member;
import com.brightline.loyalty.model.Tier;
import com.brightline.loyalty.repository.MemberRepository;
import com.brightline.loyalty.rule.bonus.SpendBonusRule;
import com.brightline.loyalty.rule.tier.StandardTierRule;
import com.brightline.loyalty.rule.tier.TierRuleRegistry;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointsCalculatorTest {

    private static final String MEMBER_ID = "m1";
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    @Mock
    private MemberRepository repository;

    private final TierRuleRegistry tierRules = new LoyaltyConfiguration().tierRuleRegistry();
    private final SpendBonusRule spendBonus = new SpendBonusRule();

    private PointsCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new PointsCalculator(repository, tierRules, spendBonus);
    }

    @ParameterizedTest(name = "{0} spending GHS {1} earns {2} points")
    @CsvSource({
        "STANDARD, 100,     10",
        "SILVER,   200,     30",
        "GOLD,     250,     50",
        "SILVER,   150,     22",
        "STANDARD, 19.99,   1",
        "STANDARD, 69.99,   6",
        "STANDARD, 70.00,   7",
        "STANDARD, 500,     50",
        "STANDARD, 500.01,  100",
        "SILVER,   500.01,  125",
        "SILVER,   600,     140",
        "GOLD,     600,     170",
        "GOLD,     1000000, 200050"
    })
    void awardsPointsForTierAndAmount(Tier tier, BigDecimal amount, int expectedPoints) {
        givenMemberWithTier(tier);

        assertEquals(expectedPoints, calculator.calculatePoints(MEMBER_ID, amount));
    }

    @ParameterizedTest(name = "GHS {0} earns no points")
    @ValueSource(strings = {"0", "9.99"})
    void awardsNoPointsBelowOnePointOfSpend(BigDecimal amount) {
        givenMemberWithTier(Tier.GOLD);

        assertEquals(0, calculator.calculatePoints(MEMBER_ID, amount));
    }

    @Test
    void throwsMemberNotFoundForUnknownMember() {
        when(repository.findById("unknown")).thenReturn(Optional.empty());

        MemberNotFoundException exception = assertThrows(MemberNotFoundException.class,
                () -> calculator.calculatePoints("unknown", ONE_HUNDRED));

        assertEquals("unknown", exception.getMemberId());
    }

    @ParameterizedTest(name = "member id \"{0}\" is rejected")
    @NullSource
    @ValueSource(strings = {"", "   "})
    void rejectsMissingMemberIdWithoutLookingUpMember(String memberId) {
        MemberNotFoundException exception = assertThrows(MemberNotFoundException.class,
                () -> calculator.calculatePoints(memberId, ONE_HUNDRED));

        assertEquals(memberId, exception.getMemberId());
        verifyNoInteractions(repository);
    }

    @ParameterizedTest(name = "GHS {0} is rejected")
    @NullSource
    @ValueSource(strings = {"-0.01", "-20", "1000000.01", "10.001"})
    void rejectsInvalidAmountWithoutLookingUpMember(BigDecimal amount) {
        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class,
                () -> calculator.calculatePoints(MEMBER_ID, amount));

        assertEquals(amount, exception.getAmount());
        verifyNoInteractions(repository);
    }

    @Test
    void propagatesRepositoryFailureUnchanged() {
        IllegalStateException failure = new IllegalStateException("database unavailable");
        when(repository.findById(MEMBER_ID)).thenThrow(failure);

        IllegalStateException thrown = assertThrows(IllegalStateException.class,
                () -> calculator.calculatePoints(MEMBER_ID, ONE_HUNDRED));

        assertSame(failure, thrown);
    }

    @Test
    void throwsUnsupportedTierWhenTierHasNoRule() {
        TierRuleRegistry standardOnly =
                new TierRuleRegistry(Map.of(Tier.STANDARD, new StandardTierRule()));
        PointsCalculator calculatorWithoutGold =
                new PointsCalculator(repository, standardOnly, spendBonus);
        givenMemberWithTier(Tier.GOLD);

        UnsupportedTierException exception = assertThrows(UnsupportedTierException.class,
                () -> calculatorWithoutGold.calculatePoints(MEMBER_ID, ONE_HUNDRED));

        assertEquals(Tier.GOLD, exception.getTier());
    }

    @Test
    void rejectsNullRepository() {
        assertThrows(NullPointerException.class,
                () -> new PointsCalculator(null, tierRules, spendBonus));
    }

    @Test
    void rejectsNullTierRules() {
        assertThrows(NullPointerException.class,
                () -> new PointsCalculator(repository, null, spendBonus));
    }

    @Test
    void rejectsNullSpendBonus() {
        assertThrows(NullPointerException.class,
                () -> new PointsCalculator(repository, tierRules, null));
    }

    private void givenMemberWithTier(Tier tier) {
        Member member = new Member(MEMBER_ID, "Kofi Mensah", tier);
        when(repository.findById(MEMBER_ID)).thenReturn(Optional.of(member));
    }
}
