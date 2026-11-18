package com.brightline.loyalty.rule.tier;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GoldTierRuleTest {

    private final GoldTierRule rule = new GoldTierRule();

    @ParameterizedTest(name = "{0} base points become {1}")
    @CsvSource({
        "0,  0",
        "1,  2",
        "25, 50"
    })
    void doublesBasePoints(int basePoints, int expectedPoints) {
        assertEquals(expectedPoints, rule.calculateTierPoints(basePoints));
    }
}
