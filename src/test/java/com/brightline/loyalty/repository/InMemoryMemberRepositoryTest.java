package com.brightline.loyalty.repository;

import com.brightline.loyalty.model.Member;
import com.brightline.loyalty.model.Tier;
import java.util.Optional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryMemberRepositoryTest {

    private final InMemoryMemberRepository repository = new InMemoryMemberRepository();

    @Test
    void findsSeededMemberById() {
        assertEquals(Optional.of(new Member("m2", "Ama Owusu", Tier.GOLD)),
                repository.findById("m2"));
    }

    @Test
    void returnsEmptyForUnknownId() {
        assertTrue(repository.findById("unknown").isEmpty());
    }
}
