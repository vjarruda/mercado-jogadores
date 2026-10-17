package br.com.passe.market.dto;

import br.com.passe.market.entity.Contract;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BuyResponse(
        @JsonUnwrapped TransactionResponse transaction,
        ContractView contract,
        BigDecimal clubCash) {

    public record ContractView(BigDecimal purchaseValue, LocalDateTime loyaltyUntil, String status) {
    }

    public static BuyResponse of(TransactionResponse transaction, Contract contract, BigDecimal clubCash) {
        return new BuyResponse(
                transaction,
                new ContractView(contract.getPurchaseValue(), contract.getLoyaltyUntil(), contract.getStatus().name()),
                clubCash);
    }
}
