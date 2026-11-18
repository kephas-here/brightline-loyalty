package com.brightline.loyalty.service;

import com.brightline.loyalty.exception.InvalidPurchaseException;
import com.brightline.loyalty.exception.MemberNotFoundException;
import com.brightline.loyalty.model.Member;
import com.brightline.loyalty.repository.MemberRepository;
import com.brightline.loyalty.rule.tier.TierRuleRegistry;

public class PointsCalculator {

    private final MemberRepository repository;
    private final TierRuleRegistry tierRules;

    public PointsCalculator(MemberRepository repository, TierRuleRegistry tierRules) {
        this.repository = repository;
        this.tierRules = tierRules;
    }

    public int calculatePoints(String memberId, double amount) {
        validateAmount(amount);
        Member member = findMember(memberId);

        // base points: one point for every GHS 10 spent
        int points = (int) (amount / 10);

        points = tierRules.ruleFor(member.tier()).calculateTierPoints(points);

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
