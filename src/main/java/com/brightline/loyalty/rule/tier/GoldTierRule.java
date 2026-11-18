package com.brightline.loyalty.rule.tier;

public class GoldTierRule implements TierRule {

    private static final int MULTIPLIER = 2;

    @Override
    public int calculateTierPoints(int basePoints) {
        return basePoints * MULTIPLIER;
    }
}
