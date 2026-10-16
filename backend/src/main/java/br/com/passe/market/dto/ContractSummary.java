package br.com.passe.market.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ContractSummary(
        BigDecimal purchaseValue,
        LocalDateTime loyaltyUntil,
        int remainingDays,
        int penaltyRate) {
}
