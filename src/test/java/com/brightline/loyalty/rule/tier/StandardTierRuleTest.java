package com.brightline.loyalty.rule.tier;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StandardTierRuleTest {

    private final StandardTierRule rule = new StandardTierRule();

    @ParameterizedTest(name = "{0} base points stay {0}")
    @ValueSource(ints = {0, 1, 25})
    void keepsBasePointsUnchanged(int basePoints) {
        assertEquals(basePoints, rule.calculateTierPoints(basePoints));
    }
}
