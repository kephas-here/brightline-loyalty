package com.brightline.loyalty;

import com.brightline.loyalty.exception.LoyaltyException;
import com.brightline.loyalty.repository.InMemoryMemberRepository;
import com.brightline.loyalty.service.PointsCalculator;

public class Demo {

    public static void main(String[] args) {
        PointsCalculator calculator = new PointsCalculator(new InMemoryMemberRepository());

        printPoints(calculator, "m1", 100);
        printPoints(calculator, "m2", 250);
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
