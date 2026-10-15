package br.com.passe.shared.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Money {
    public static final BigDecimal ONE_COIN = new BigDecimal("1.00");

    private Money() {
    }

    public static BigDecimal round(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}