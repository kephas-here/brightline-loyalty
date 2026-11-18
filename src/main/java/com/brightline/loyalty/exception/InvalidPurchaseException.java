package com.brightline.loyalty.exception;

public class InvalidPurchaseException extends LoyaltyException {

    private final double amount;

    public InvalidPurchaseException(double amount) {
        super("Purchase amount must be a finite number of zero or more, but was " + amount);
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }
}
