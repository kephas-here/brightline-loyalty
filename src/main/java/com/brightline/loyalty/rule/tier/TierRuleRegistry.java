package com.brightline.loyalty.rule.tier;

import com.brightline.loyalty.exception.UnsupportedTierException;
import com.brightline.loyalty.model.Tier;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class TierRuleRegistry {

    private final Map<Tier, TierRule> rules = new EnumMap<>(Tier.class);

    public TierRuleRegistry(Map<Tier, TierRule> rules) {
        Objects.requireNonNull(rules, "Tier rules must not be null");
        rules.forEach((tier, rule) -> this.rules.put(tier,
                Objects.requireNonNull(rule, "Rule for " + tier + " must not be null")));
    }

    public TierRule ruleFor(Tier tier) {
        TierRule rule = rules.get(tier);
        if (rule == null) {
            throw new UnsupportedTierException(tier);
        }
        return rule;
    }
}
