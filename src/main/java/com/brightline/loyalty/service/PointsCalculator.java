package com.brightline.loyalty.service;

import com.brightline.loyalty.exception.InvalidPurchaseException;
import com.brightline.loyalty.exception.MemberNotFoundException;
import com.brightline.loyalty.model.Member;
import com.brightline.loyalty.model.Tier;
import com.brightline.loyalty.repository.MemberRepository;

public class PointsCalculator {

    private final MemberRepository repository;

    public PointsCalculator(MemberRepository repository) {
        this.repository = repository;
    }

    public int calculatePoints(String memberId, double amount) {
        validateAmount(amount);
        Member member = findMember(memberId);

        // base points: one point for every GHS 10 spent
        int points = (int) (amount / 10);

        // apply the member's tier multiplier
        if (member.tier() == Tier.STANDARD) {
            points = points * 1;
        } else if (member.tier() == Tier.SILVER) {
            points = (int) (points * 1.5);
        } else if (member.tier() == Tier.GOLD) {
            points = points * 2;
        }

        // loyalty bonus for big spenders
        if (amount > 500) {
            points = points + 50;
        }

        System.out.println("Awarded " + points + " points to " + member.name());
        return points;
    }

    private void validateAmount(double amount) {
        if (!Double.isFinite(amount) || amount < 0) {
            throw new InvalidPurchaseException(amount);
        }
    }

    private Member findMember(String memberId) {
        return repository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
    }
}
