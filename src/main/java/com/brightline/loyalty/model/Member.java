package com.brightline.loyalty.model;

import java.util.Objects;

public record Member(String id, String name, Tier tier) {

    public Member {
        requireText(id, "id");
        requireText(name, "name");
        Objects.requireNonNull(tier, "Member tier must not be null");
    }

    private static void requireText(String value, String field) {
        Objects.requireNonNull(value, "Member " + field + " must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("Member " + field + " must not be blank");
        }
    }
}
