package br.com.passe.market.service;

import java.time.Duration;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class LoyaltyRules {

    private static final long SECONDS_PER_DAY = 86_400;

    public int remainingDays(LocalDateTime loyaltyUntil, LocalDateTime now) {
        Duration left = Duration.between(now, loyaltyUntil);
        if (left.isZero() || left.isNegative()) {
            return 0;
        }
        long seconds = left.getSeconds() + (left.getNano() > 0 ? 1 : 0);
        return (int) ((seconds + SECONDS_PER_DAY - 1) / SECONDS_PER_DAY);
    }

    public int penaltyRate(int remainingDays) {
        if (remainingDays == 0) {
            return 0;
        }
        if (remainingDays <= 30) {
            return 5;
        }
        return remainingDays <= 180 ? 10 : 20;
    }
}
