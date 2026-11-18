package com.brightline.loyalty;

import com.brightline.loyalty.config.LoyaltyConfiguration;
import com.brightline.loyalty.exception.LoyaltyException;
import com.brightline.loyalty.repository.InMemoryMemberRepository;
import com.brightline.loyalty.service.PointsCalculator;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Demo {

    private static final Logger LOGGER = Logger.getLogger(Demo.class.getName());

    public static void main(String[] args) {
        PointsCalculator calculator =
                new LoyaltyConfiguration().pointsCalculator(new InMemoryMemberRepository());

        reportPoints(calculator, "m1", "100");
        reportPoints(calculator, "m2", "250");
        reportPoints(calculator, "m3", "200");
        reportPoints(calculator, "m2", "600");
        reportPoints(calculator, "unknown", "100");
        reportPoints(calculator, "m1", "-20");
        reportPoints(calculator, "m1", "10.001");
    }

    private static void reportPoints(PointsCalculator calculator, String memberId, String amount) {
        try {
            int points = calculator.calculatePoints(memberId, new BigDecimal(amount));
            LOGGER.info(() -> memberId + ", GHS " + amount + " -> " + points + " points");
        } catch (LoyaltyException e) {
            LOGGER.warning(() -> memberId + ", GHS " + amount + " -> rejected: " + e.getMessage());
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, e, () -> memberId + ", GHS " + amount
                    + " -> failed: unexpected error");
        }
    }
}
