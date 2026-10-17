package br.com.passe.market.dto;

import br.com.passe.market.entity.MarketTransaction;
import br.com.passe.market.enums.TransactionType;
import br.com.passe.shared.util.Money;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

public record TransactionSummary(
        BigDecimal totalSpent,
        BigDecimal totalReceived,
        BigDecimal totalFees,
        BigDecimal totalPenalties,
        BigDecimal totalProfit) {

    public static TransactionSummary of(List<MarketTransaction> transactions) {
        return new TransactionSummary(
                // total pago: valor + taxa das compras
                sum(transactions, TransactionType.COMPRA, t -> t.getValue().add(t.getFee())),
                // total recebido: crédito líquido das vendas
                sum(transactions, TransactionType.VENDA, MarketTransaction::getCashChange),
                // taxas e comissões de todas as transações
                sum(transactions, null, MarketTransaction::getFee),
                sum(transactions, TransactionType.VENDA, MarketTransaction::getPenalty),
                sum(transactions, TransactionType.VENDA, MarketTransaction::getProfit));
    }

    private static BigDecimal sum(List<MarketTransaction> transactions, TransactionType type,
                                  Function<MarketTransaction, BigDecimal> field) {
        BigDecimal total = transactions.stream()
                .filter(t -> type == null || t.getType() == type)
                .map(field)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return Money.round(total);
    }
}
