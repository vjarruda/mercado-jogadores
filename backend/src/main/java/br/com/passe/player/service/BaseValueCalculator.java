package br.com.passe.player.service;

import br.com.passe.shared.util.Money;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BaseValueCalculator {

    private static final int YOUNG_PROMISE_MAX_AGE = 21;

    private static final BigDecimal FIRST_TIER_RATE = new BigDecimal("0.50");
    private static final BigDecimal SECOND_TIER_RATE = new BigDecimal("2.00");
    private static final BigDecimal THIRD_TIER_RATE = new BigDecimal("6.00");

    public BigDecimal calculate(int overall, int potential, int ageAtRegistration) {
        BigDecimal overallPart = overallPart(overall);

        if (ageAtRegistration <= YOUNG_PROMISE_MAX_AGE) {
            int factor = 100 + potential - overall;
            overallPart = overallPart.multiply(BigDecimal.valueOf(factor)).movePointLeft(2);
        }

        BigDecimal rounded = Money.round(overallPart);
        return rounded.compareTo(Money.ONE_COIN) < 0 ? Money.ONE_COIN : rounded;
    }

    private BigDecimal overallPart(int overall) {
        BigDecimal part = BigDecimal.ZERO;
        part = part.add(FIRST_TIER_RATE.multiply(BigDecimal.valueOf(pointsInRange(overall, 41, 60))));
        part = part.add(SECOND_TIER_RATE.multiply(BigDecimal.valueOf(pointsInRange(overall, 61, 75))));
        part = part.add(THIRD_TIER_RATE.multiply(BigDecimal.valueOf(pointsInRange(overall, 76, 99))));
        return part;
    }

    //quantos pontos de overall caem
    private int pointsInRange(int overall, int from, int to) {
        return Math.max(0, Math.min(overall, to) - from + 1);
    }
}
