package br.com.passe.market.service;

import br.com.passe.club.service.ClubService;
import br.com.passe.market.dto.TransactionFilter;
import br.com.passe.market.dto.TransactionListResponse;
import br.com.passe.market.dto.TransactionResponse;
import br.com.passe.market.dto.TransactionSummary;
import br.com.passe.market.entity.MarketTransaction;
import br.com.passe.market.repository.TransactionRepository;
import br.com.passe.player.service.PlayerService;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionQueryService {

    //da mais recente para a mais antiga; no empate, id decrescente
    private static final Comparator<MarketTransaction> NEWEST_FIRST = Comparator
            .comparing(MarketTransaction::getCreatedAt)
            .thenComparing(MarketTransaction::getId)
            .reversed();

    private final ClubService clubService;
    private final PlayerService playerService;
    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public TransactionListResponse list(String rawClubId, TransactionFilter filter) {
        List<MarketTransaction> transactions = filtered(rawClubId, filter);
        return new TransactionListResponse(
                transactions.stream().map(TransactionResponse::from).toList(),
                TransactionSummary.of(transactions));
    }

    @Transactional(readOnly = true)
    public List<MarketTransaction> filtered(String rawClubId, TransactionFilter filter) {
        Integer clubId = clubService.findClub(rawClubId).getId();
        if (filter.playerId() != null) {
            // jogador inexistente ou excluído gera 404
            playerService.findActive(String.valueOf(filter.playerId()));
        }
        return transactionRepository.findAllByClubId(clubId).stream()
                .filter(filter::matches)
                .sorted(NEWEST_FIRST)
                .toList();
    }
}
