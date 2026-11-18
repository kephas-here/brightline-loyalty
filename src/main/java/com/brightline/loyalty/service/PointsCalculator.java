package com.brightline.loyalty.service;

import com.brightline.loyalty.exception.InvalidPurchaseException;
import com.brightline.loyalty.exception.MemberNotFoundException;
import com.brightline.loyalty.model.Member;
import com.brightline.loyalty.repository.MemberRepository;
import com.brightline.loyalty.rule.bonus.SpendBonusRule;
import com.brightline.loyalty.rule.tier.TierRuleRegistry;

public class PointsCalculator {

    private static final int SPEND_PER_POINT = 10;

    private final MemberRepository repository;
    private final TierRuleRegistry tierRules;
    private final SpendBonusRule spendBonus;

    public PointsCalculator(MemberRepository repository, TierRuleRegistry tierRules,
                            SpendBonusRule spendBonus) {
        this.repository = repository;
        this.tierRules = tierRules;
        this.spendBonus = spendBonus;
    }

    public int calculatePoints(String memberId, double amount) {
        validateAmount(amount);
        Member member = findMember(memberId);
        int tierPoints = tierRules.ruleFor(member.tier())
                .calculateTierPoints(basePointsFor(amount));
        return tierPoints + spendBonus.calculateBonusPoints(amount);
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

    private int basePointsFor(double amount) {
        return (int) (amount / SPEND_PER_POINT);
    }
}
