package br.com.passe.market.service;

import br.com.passe.shared.util.Money;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class SaleCalculator {

    private static final BigDecimal MAX_PENALTY = new BigDecimal("10.00");
    private static final BigDecimal COMMISSION_BAND = new BigDecimal("10.00");
    private static final BigDecimal LOW_RATE = new BigDecimal("0.10");
    private static final BigDecimal HIGH_RATE = new BigDecimal("0.20");

    public BigDecimal penalty(BigDecimal marketValue, int penaltyRate) {
        BigDecimal penalty = Money.round(marketValue.multiply(BigDecimal.valueOf(penaltyRate)).movePointLeft(2));
        return penalty.min(MAX_PENALTY);
    }

    public BigDecimal commission(BigDecimal profit) {
        if (profit.signum() <= 0) {
            return Money.round(BigDecimal.ZERO);
        }
        BigDecimal lowPart = profit.min(COMMISSION_BAND).multiply(LOW_RATE);
        BigDecimal highPart = profit.subtract(COMMISSION_BAND).max(BigDecimal.ZERO).multiply(HIGH_RATE);
        return Money.round(lowPart.add(highPart));
    }
}