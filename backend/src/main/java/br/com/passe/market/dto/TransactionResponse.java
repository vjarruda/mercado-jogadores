package br.com.passe.market.dto;

import br.com.passe.market.entity.MarketTransaction;
import br.com.passe.market.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Integer id,
        Integer clubId,
        Integer playerId,
        String playerName,
        TransactionType type,
        BigDecimal value,
        BigDecimal fee,
        BigDecimal penalty,
        BigDecimal profit,
        BigDecimal cashChange,
        LocalDateTime createdAt) {

    public static TransactionResponse from(MarketTransaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getClub().getId(),
                transaction.getPlayer().getId(),
                transaction.getPlayerName(),
                transaction.getType(),
                transaction.getValue(),
                transaction.getFee(),
                transaction.getPenalty(),
                transaction.getProfit(),
                transaction.getCashChange(),
                transaction.getCreatedAt());
    }
}
