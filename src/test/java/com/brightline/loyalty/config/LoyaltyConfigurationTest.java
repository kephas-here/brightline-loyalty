package com.brightline.loyalty.config;

import com.brightline.loyalty.model.Member;
import com.brightline.loyalty.model.Tier;
import com.brightline.loyalty.rule.tier.GoldTierRule;
import com.brightline.loyalty.rule.tier.SilverTierRule;
import com.brightline.loyalty.rule.tier.StandardTierRule;
import com.brightline.loyalty.rule.tier.TierRuleRegistry;
import com.brightline.loyalty.service.PointsCalculator;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class LoyaltyConfigurationTest {

    private final LoyaltyConfiguration configuration = new LoyaltyConfiguration();

    @ParameterizedTest(name = "{0} has a registered rule")
    @EnumSource(Tier.class)
    void registersRuleForEveryTier(Tier tier) {
        TierRuleRegistry registry = configuration.tierRuleRegistry();

        assertDoesNotThrow(() -> registry.ruleFor(tier));
    }

    @Test
    void mapsEachTierToItsOwnRule() {
        TierRuleRegistry registry = configuration.tierRuleRegistry();

        assertAll(
                () -> assertInstanceOf(StandardTierRule.class, registry.ruleFor(Tier.STANDARD)),
                () -> assertInstanceOf(SilverTierRule.class, registry.ruleFor(Tier.SILVER)),
                () -> assertInstanceOf(GoldTierRule.class, registry.ruleFor(Tier.GOLD)));
    }

    @Test
    void buildsCalculatorThatUsesGivenRepository() {
        Member goldMember = new Member("m2", "Ama Owusu", Tier.GOLD);
        PointsCalculator calculator =
                configuration.pointsCalculator(id -> Optional.of(goldMember));

        assertEquals(170, calculator.calculatePoints("m2", new BigDecimal("600")));
    }
}
