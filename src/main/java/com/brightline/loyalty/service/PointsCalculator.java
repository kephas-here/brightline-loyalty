package com.brightline.loyalty.service;

import com.brightline.loyalty.exception.InvalidPurchaseException;
import com.brightline.loyalty.exception.MemberNotFoundException;
import com.brightline.loyalty.model.Member;
import com.brightline.loyalty.repository.MemberRepository;
import com.brightline.loyalty.rule.bonus.SpendBonusRule;
import com.brightline.loyalty.rule.tier.TierRuleRegistry;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class PointsCalculator {

    private static final BigDecimal SPEND_PER_POINT = new BigDecimal("10");
    private static final BigDecimal MAX_PURCHASE_AMOUNT = new BigDecimal("1000000");
    private static final int MONEY_DECIMAL_PLACES = 2;

    private final MemberRepository repository;
    private final TierRuleRegistry tierRules;
    private final SpendBonusRule spendBonus;

    public PointsCalculator(MemberRepository repository, TierRuleRegistry tierRules,
                            SpendBonusRule spendBonus) {
        this.repository = repository;
        this.tierRules = tierRules;
        this.spendBonus = spendBonus;
    }

    public int calculatePoints(String memberId, BigDecimal amount) {
        validateAmount(amount);
        Member member = findMember(memberId);
        int tierPoints = tierRules.ruleFor(member.tier())
                .calculateTierPoints(basePointsFor(amount));
        return tierPoints + spendBonus.calculateBonusPoints(amount);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new InvalidPurchaseException(null, "an amount is required");
        }
        if (amount.signum() < 0) {
            throw new InvalidPurchaseException(amount, "must not be negative");
        }
        if (amount.compareTo(MAX_PURCHASE_AMOUNT) > 0) {
            throw new InvalidPurchaseException(amount,
                    "must not exceed GHS " + MAX_PURCHASE_AMOUNT);
        }
        if (amount.stripTrailingZeros().scale() > MONEY_DECIMAL_PLACES) {
            throw new InvalidPurchaseException(amount,
                    "must have at most " + MONEY_DECIMAL_PLACES + " decimal places");
        }
    }

    private Member findMember(String memberId) {
        return repository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
    }

    private int basePointsFor(BigDecimal amount) {
        return amount.divide(SPEND_PER_POINT, 0, RoundingMode.DOWN).intValueExact();
    }
}
