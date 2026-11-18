package com.brightline.loyalty.rule.bonus;

import java.math.BigDecimal;

public class SpendBonusRule {

    private static final BigDecimal BONUS_THRESHOLD = new BigDecimal("500");
    private static final int BONUS_POINTS = 50;

    public int calculateBonusPoints(BigDecimal amount) {
        return amount.compareTo(BONUS_THRESHOLD) > 0 ? BONUS_POINTS : 0;
    }
}
