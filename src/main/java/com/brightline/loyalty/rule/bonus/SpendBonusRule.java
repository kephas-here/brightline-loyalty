package com.brightline.loyalty.rule.bonus;

public class SpendBonusRule {

    private static final double BONUS_THRESHOLD = 500;
    private static final int BONUS_POINTS = 50;

    public int calculateBonusPoints(double amount) {
        return amount > BONUS_THRESHOLD ? BONUS_POINTS : 0;
    }
}
