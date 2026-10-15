package br.com.passe.player.service;

import br.com.passe.player.enums.PlayerCondition;
import br.com.passe.shared.util.Money;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MarketValueCalculator {

    private static final BigDecimal MAX_DISCOUNT = new BigDecimal("-40");
    private static final BigDecimal YOUNG_BONUS = new BigDecimal("10");
    private static final BigDecimal OLD_PENALTY = new BigDecimal("-10");
    private static final BigDecimal LOW_AGILITY_EXTRA_DISCOUNT = new BigDecimal("10");

    public BigDecimal calculate(BigDecimal baseValue, PlayerCondition condition, int agility, int age) {
        BigDecimal conditionDiscount = conditionDiscount(condition, agility);
        BigDecimal totalPercent = ageAdjustment(age).subtract(conditionDiscount).max(MAX_DISCOUNT);

        BigDecimal factor = BigDecimal.valueOf(100).add(totalPercent);
        BigDecimal value = Money.round(baseValue.multiply(factor).movePointLeft(2));

        return value.compareTo(Money.ONE_COIN) < 0 ? Money.ONE_COIN : value;
    }

    //a agilidade so m uda o desconto quando o jogador nao esta apto
    private BigDecimal conditionDiscount(PlayerCondition condition, int agility) {
        BigDecimal discount = switch (condition) {
            case APTO -> BigDecimal.ZERO;
            case MAL_CONDICIONADO -> new BigDecimal("15");
            case DOENTE -> new BigDecimal("30");
        };

        if (condition == PlayerCondition.APTO) {
            return discount;
        }
        if (agility >= 80) {
            return discount.divide(BigDecimal.valueOf(2));
        }
        if (agility <= 30) {
            return discount.add(LOW_AGILITY_EXTRA_DISCOUNT);
        }
        return discount;
    }

    private BigDecimal ageAdjustment(int age) {
        if (age <= 21) {
            return YOUNG_BONUS;
        }
        if (age >= 32) {
            return OLD_PENALTY;
        }
        return BigDecimal.ZERO;
    }
}
