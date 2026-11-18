package com.brightline.loyalty.rule.tier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GoldTierRuleTest {

    private final GoldTierRule rule = new GoldTierRule();

    @ParameterizedTest(name = "{0} base points become {1}")
    @CsvSource({
        "0,       0",
        "1,       2",
        "25,      50",
        "1000000, 2000000"
    })
    void doublesBasePoints(int basePoints, int expectedPoints) {
        assertEquals(expectedPoints, rule.calculateTierPoints(basePoints));
    }

    @Test
    void throwsInsteadOfOverflowing() {
        assertThrows(ArithmeticException.class,
                () -> rule.calculateTierPoints(Integer.MAX_VALUE));
    }
}
