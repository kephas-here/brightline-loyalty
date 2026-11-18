package com.brightline.loyalty.rule.bonus;

import java.math.BigDecimal;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpendBonusRuleTest {

    private final SpendBonusRule rule = new SpendBonusRule();

    @ParameterizedTest(name = "GHS {0} earns no bonus")
    @ValueSource(strings = {"0", "499.99", "500", "500.00"})
    void awardsNoBonusUpToThreshold(BigDecimal amount) {
        assertEquals(0, rule.calculateBonusPoints(amount));
    }

    @ParameterizedTest(name = "GHS {0} earns the 50 point bonus")
    @ValueSource(strings = {"500.01", "1000"})
    void awardsBonusAboveThreshold(BigDecimal amount) {
        assertEquals(50, rule.calculateBonusPoints(amount));
    }
}
