package br.com.passe.market.dto;

import java.util.List;

public record TransactionListResponse(List<TransactionResponse> data, TransactionSummary summary) {
}
