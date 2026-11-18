package com.brightline.loyalty.config;

import com.brightline.loyalty.model.Tier;
import com.brightline.loyalty.repository.MemberRepository;
import com.brightline.loyalty.rule.bonus.SpendBonusRule;
import com.brightline.loyalty.rule.tier.GoldTierRule;
import com.brightline.loyalty.rule.tier.SilverTierRule;
import com.brightline.loyalty.rule.tier.StandardTierRule;
import com.brightline.loyalty.rule.tier.TierRuleRegistry;
import com.brightline.loyalty.service.PointsCalculator;
import java.util.Map;

public class LoyaltyConfiguration {

    public TierRuleRegistry tierRuleRegistry() {
        return new TierRuleRegistry(Map.of(
                Tier.STANDARD, new StandardTierRule(),
                Tier.SILVER, new SilverTierRule(),
                Tier.GOLD, new GoldTierRule()));
    }

    public PointsCalculator pointsCalculator(MemberRepository repository) {
        return new PointsCalculator(repository, tierRuleRegistry(), new SpendBonusRule());
    }
}
