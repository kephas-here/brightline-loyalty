package com.brightline.loyalty.exception;

public abstract class LoyaltyException extends RuntimeException {

    protected LoyaltyException(String message) {
        super(message);
    }
}
