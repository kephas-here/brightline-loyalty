package com.brightline.loyalty.rule.tier;

public class StandardTierRule implements TierRule {

    @Override
    public int calculateTierPoints(int basePoints) {
        return basePoints;
    }
}
