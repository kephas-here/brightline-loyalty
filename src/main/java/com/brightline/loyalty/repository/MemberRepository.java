package com.brightline.loyalty.repository;

import com.brightline.loyalty.model.Member;
import java.util.Optional;

public interface MemberRepository {
    Optional<Member> findById(String id);
}
