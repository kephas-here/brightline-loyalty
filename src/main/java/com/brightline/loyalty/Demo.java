package com.brightline.loyalty;

import com.brightline.loyalty.exception.LoyaltyException;
import com.brightline.loyalty.model.Tier;
import com.brightline.loyalty.repository.InMemoryMemberRepository;
import com.brightline.loyalty.rule.bonus.SpendBonusRule;
import com.brightline.loyalty.rule.tier.GoldTierRule;
import com.brightline.loyalty.rule.tier.SilverTierRule;
import com.brightline.loyalty.rule.tier.StandardTierRule;
import com.brightline.loyalty.rule.tier.TierRuleRegistry;
import com.brightline.loyalty.service.PointsCalculator;
import java.util.Map;

public class Demo {

    public static void main(String[] args) {
        TierRuleRegistry tierRules = new TierRuleRegistry(Map.of(
                Tier.STANDARD, new StandardTierRule(),
                Tier.SILVER, new SilverTierRule(),
                Tier.GOLD, new GoldTierRule()));
        PointsCalculator calculator = new PointsCalculator(
                new InMemoryMemberRepository(), tierRules, new SpendBonusRule());

        printPoints(calculator, "m1", 100);
        printPoints(calculator, "m2", 250);
        printPoints(calculator, "m3", 200);
        printPoints(calculator, "m2", 600);
        printPoints(calculator, "unknown", 100);
        printPoints(calculator, "m1", -20);
    }

    private static void printPoints(PointsCalculator calculator, String memberId, double amount) {
        try {
            int points = calculator.calculatePoints(memberId, amount);
            System.out.println(memberId + ", GHS " + amount + " -> " + points + " points");
        } catch (LoyaltyException e) {
            System.out.println(memberId + ", GHS " + amount + " -> rejected: " + e.getMessage());
        }
    }
}
