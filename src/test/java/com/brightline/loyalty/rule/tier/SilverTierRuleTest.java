package com.brightline.loyalty.rule.tier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SilverTierRuleTest {

    private final SilverTierRule rule = new SilverTierRule();

    @ParameterizedTest(name = "{0} base points become {1}")
    @CsvSource({
        "0,       0",
        "10,      15",
        "20,      30",
        "1000000, 1500000"
    })
    void multipliesBasePointsByOneAndAHalf(int basePoints, int expectedPoints) {
        assertEquals(expectedPoints, rule.calculateTierPoints(basePoints));
    }

    @ParameterizedTest(name = "{0} base points round down to {1}")
    @CsvSource({
        "1,  1",
        "15, 22"
    })
    void roundsFractionalPointsDown(int basePoints, int expectedPoints) {
        assertEquals(expectedPoints, rule.calculateTierPoints(basePoints));
    }

    @Test
    void throwsInsteadOfOverflowing() {
        assertThrows(ArithmeticException.class,
                () -> rule.calculateTierPoints(Integer.MAX_VALUE));
    }
}
