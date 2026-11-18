package com.brightline.loyalty.rule.tier;

import com.brightline.loyalty.exception.UnsupportedTierException;
import com.brightline.loyalty.model.Tier;
import java.util.EnumMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TierRuleRegistryTest {

    @Test
    void returnsRuleRegisteredForTier() {
        TierRule goldRule = new GoldTierRule();
        TierRuleRegistry registry = new TierRuleRegistry(Map.of(
                Tier.STANDARD, new StandardTierRule(),
                Tier.GOLD, goldRule));

        assertSame(goldRule, registry.ruleFor(Tier.GOLD));
    }

    @Test
    void throwsUnsupportedTierForUnregisteredTier() {
        TierRuleRegistry registry =
                new TierRuleRegistry(Map.of(Tier.STANDARD, new StandardTierRule()));

        UnsupportedTierException exception = assertThrows(UnsupportedTierException.class,
                () -> registry.ruleFor(Tier.SILVER));

        assertEquals(Tier.SILVER, exception.getTier());
    }

    @Test
    void ignoresChangesToSourceMapAfterConstruction() {
        Map<Tier, TierRule> source = new EnumMap<>(Tier.class);
        source.put(Tier.STANDARD, new StandardTierRule());
        TierRuleRegistry registry = new TierRuleRegistry(source);

        source.put(Tier.GOLD, new GoldTierRule());

        assertThrows(UnsupportedTierException.class, () -> registry.ruleFor(Tier.GOLD));
    }

    @Test
    void rejectsNullRuleMap() {
        assertThrows(NullPointerException.class, () -> new TierRuleRegistry(null));
    }

    @Test
    void rejectsNullRuleForTier() {
        Map<Tier, TierRule> source = new EnumMap<>(Tier.class);
        source.put(Tier.GOLD, null);

        assertThrows(NullPointerException.class, () -> new TierRuleRegistry(source));
    }
}
