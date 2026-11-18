package com.brightline.loyalty.repository;

import com.brightline.loyalty.model.Member;

public interface MemberRepository {
    Member findById(String id);
}
