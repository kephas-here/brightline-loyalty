package com.brightline.loyalty.exception;

import com.brightline.loyalty.model.Tier;

public class UnsupportedTierException extends LoyaltyException {

    private final Tier tier;

    public UnsupportedTierException(Tier tier) {
        super("No points rule registered for tier " + tier);
        this.tier = tier;
    }

    public Tier getTier() {
        return tier;
    }
}
