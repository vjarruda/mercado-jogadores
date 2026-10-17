package br.com.passe.market.service;

import br.com.passe.market.entity.MarketTransaction;
import br.com.passe.shared.config.BrasiliaDateTimeSerializer;
import br.com.passe.shared.util.Money;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TransactionCsvExporter {

    private static final String HEADER =
            "data_hora;tipo;jogador;valor;taxa_ou_comissao;multa;lucro;variacao_caixa";

    public String export(List<MarketTransaction> transactions) {
        StringBuilder csv = new StringBuilder(HEADER).append('\n');
        // cada linha termina em \n, inclusive a última
        transactions.forEach(transaction -> csv.append(line(transaction)).append('\n'));
        return csv.toString();
    }

    private String line(MarketTransaction transaction) {
        return String.join(";",
                BrasiliaDateTimeSerializer.FORMAT.format(transaction.getCreatedAt()),
                transaction.getType().name(),
                escape(transaction.getPlayerName()),
                decimal(transaction.getValue()),
                decimal(transaction.getFee()),
                decimal(transaction.getPenalty()),
                decimal(transaction.getProfit()),
                decimal(transaction.getCashChange()));
    }

    private String decimal(BigDecimal value) {
        return value == null ? "" : Money.round(value).toPlainString().replace('.', ',');
    }

    private String escape(String text) {
        if (!text.contains(";") && !text.contains("\"")) {
            return text;
        }
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }
}
