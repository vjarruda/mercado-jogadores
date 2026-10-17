package br.com.passe.market.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.math.BigDecimal;

public record SellResponse(
        @JsonUnwrapped TransactionResponse transaction,
        BigDecimal purchaseValue,
        int remainingDays,
        int penaltyRate,
        String contractStatus,
        BigDecimal clubCash) {
}
