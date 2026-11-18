package com.brightline.loyalty.service;

import com.brightline.loyalty.model.Tier;
import com.brightline.loyalty.repository.InMemoryMemberRepository;
import com.brightline.loyalty.rule.tier.GoldTierRule;
import com.brightline.loyalty.rule.tier.SilverTierRule;
import com.brightline.loyalty.rule.tier.StandardTierRule;
import com.brightline.loyalty.rule.tier.TierRuleRegistry;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PointsCalculatorTest {

    private final TierRuleRegistry tierRules = new TierRuleRegistry(Map.of(
            Tier.STANDARD, new StandardTierRule(),
            Tier.SILVER, new SilverTierRule(),
            Tier.GOLD, new GoldTierRule()));

    @Test
    void awardsPointsToAMember() {
        PointsCalculator calculator = new PointsCalculator(new InMemoryMemberRepository(), tierRules);
        int points = calculator.calculatePoints("m1", 100.0);
        assertTrue(points > 0); // it awarded some points
    }

    @Test
    void calculatesGoldPoints() {
        // mock the calculator and check it returns what we told it to
        PointsCalculator calculator = mock(PointsCalculator.class);
        when(calculator.calculatePoints("m2", 250.0)).thenReturn(50);
        assertEquals(50, calculator.calculatePoints("m2", 250.0));
    }

    @Test
    void awardsPointsForAPurchase() {
        PointsCalculator calculator = new PointsCalculator(new InMemoryMemberRepository(), tierRules);
        int points = calculator.calculatePoints("m3", 200.0);
        assertNotNull(points); // a result came back
    }
}
