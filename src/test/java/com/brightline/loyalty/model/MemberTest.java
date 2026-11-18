package com.brightline.loyalty.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MemberTest {

    @Test
    void keepsValidDetails() {
        Member member = new Member("m1", "Kofi Mensah", Tier.GOLD);

        assertEquals("m1", member.id());
        assertEquals("Kofi Mensah", member.name());
        assertEquals(Tier.GOLD, member.tier());
    }

    @Test
    void rejectsNullId() {
        assertThrows(NullPointerException.class,
                () -> new Member(null, "Kofi Mensah", Tier.GOLD));
    }

    @ParameterizedTest(name = "id \"{0}\" is rejected")
    @ValueSource(strings = {"", "   "})
    void rejectsBlankId(String id) {
        assertThrows(IllegalArgumentException.class,
                () -> new Member(id, "Kofi Mensah", Tier.GOLD));
    }

    @Test
    void rejectsNullName() {
        assertThrows(NullPointerException.class, () -> new Member("m1", null, Tier.GOLD));
    }

    @ParameterizedTest(name = "name \"{0}\" is rejected")
    @ValueSource(strings = {"", "   "})
    void rejectsBlankName(String name) {
        assertThrows(IllegalArgumentException.class, () -> new Member("m1", name, Tier.GOLD));
    }

    @Test
    void rejectsNullTier() {
        assertThrows(NullPointerException.class, () -> new Member("m1", "Kofi Mensah", null));
    }
}
