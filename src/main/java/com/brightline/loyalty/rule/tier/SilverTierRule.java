package com.brightline.loyalty.rule.tier;

public class SilverTierRule implements TierRule {

    private static final double MULTIPLIER = 1.5;

    @Override
    public int calculateTierPoints(int basePoints) {
        return (int) (basePoints * MULTIPLIER);
    }
}
