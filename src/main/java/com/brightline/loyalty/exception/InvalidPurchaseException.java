package com.brightline.loyalty.exception;

import java.math.BigDecimal;

public class InvalidPurchaseException extends LoyaltyException {

    private final BigDecimal amount;

    public InvalidPurchaseException(BigDecimal amount, String reason) {
        super("Invalid purchase amount " + amount + ": " + reason);
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
