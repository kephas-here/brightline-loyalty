package com.brightline.loyalty.exception;

public class MemberNotFoundException extends LoyaltyException {

    private final String memberId;

    public MemberNotFoundException(String memberId) {
        super("No member found with id " + memberId);
        this.memberId = memberId;
    }

    public String getMemberId() {
        return memberId;
    }
}
