package com.brightline.loyalty.rule.tier;

public class SilverTierRule implements TierRule {

    private static final int MULTIPLIER_NUMERATOR = 3;
    private static final int MULTIPLIER_DENOMINATOR = 2;

    @Override
    public int calculateTierPoints(int basePoints) {
        return Math.multiplyExact(basePoints, MULTIPLIER_NUMERATOR) / MULTIPLIER_DENOMINATOR;
    }
}
