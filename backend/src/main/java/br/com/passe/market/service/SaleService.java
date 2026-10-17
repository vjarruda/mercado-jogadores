package br.com.passe.market.service;

import br.com.passe.club.entity.Club;
import br.com.passe.market.dto.SellResponse;
import br.com.passe.market.dto.TransactionResponse;
import br.com.passe.market.entity.Contract;
import br.com.passe.market.entity.MarketTransaction;
import br.com.passe.market.enums.ContractStatus;
import br.com.passe.market.enums.TransactionType;
import br.com.passe.market.repository.ContractRepository;
import br.com.passe.market.repository.TransactionRepository;
import br.com.passe.player.entity.Player;
import br.com.passe.player.service.PlayerService;
import br.com.passe.player.service.utils.MarketValueCalculator;
import br.com.passe.shared.exception.ConflictException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final PlayerService playerService;
    private final ContractRepository contractRepository;
    private final TransactionRepository transactionRepository;
    private final MarketValueCalculator marketValueCalculator;
    private final LoyaltyRules loyaltyRules;
    private final SaleCalculator saleCalculator;

    @Transactional
    public SellResponse sell(String rawPlayerId) {
        Player player = playerService.findActive(rawPlayerId);
        if (player.isFree()) {
            throw new ConflictException("A free player cannot be sold.");
        }

        LocalDateTime now = LocalDateTime.now();
        Club club = player.getClub();
        Contract contract = contractRepository.findByPlayerIdAndStatus(player.getId(), ContractStatus.ATIVO)
                .orElseThrow(() -> new IllegalStateException("Player " + player.getId() + " has no active contract."));

        BigDecimal value = marketValueCalculator.calculate(
                player.getBaseValue(), player.getCondition(), player.getAgility(), player.getAge());
        int remainingDays = loyaltyRules.remainingDays(contract.getLoyaltyUntil(), now);
        int penaltyRate = loyaltyRules.penaltyRate(remainingDays);
        BigDecimal penalty = saleCalculator.penalty(value, penaltyRate);
        BigDecimal profit = value.subtract(contract.getPurchaseValue());
        BigDecimal commission = saleCalculator.commission(profit);
        BigDecimal credit = value.subtract(penalty).subtract(commission);

        club.setCash(club.getCash().add(credit));
        player.setClub(null);
        contract.setStatus(penaltyRate > 0 ? ContractStatus.RESCINDIDO : ContractStatus.ENCERRADO);
        contract.setEndedAt(now);

        MarketTransaction transaction = transactionRepository.save(
                newTransaction(player, club, value, commission, penalty, profit, credit, now));

        return new SellResponse(TransactionResponse.from(transaction), contract.getPurchaseValue(),
                remainingDays, penaltyRate, contract.getStatus().name(), club.getCash());
    }

    private MarketTransaction newTransaction(Player player, Club club, BigDecimal value, BigDecimal commission,
                                             BigDecimal penalty, BigDecimal profit, BigDecimal credit,
                                             LocalDateTime now) {
        MarketTransaction transaction = new MarketTransaction();
        transaction.setClub(club);
        transaction.setPlayer(player);
        transaction.setPlayerName(player.getName());
        transaction.setType(TransactionType.VENDA);
        transaction.setValue(value);
        transaction.setFee(commission);
        transaction.setPenalty(penalty);
        transaction.setProfit(profit);
        transaction.setCashChange(credit);
        transaction.setCreatedAt(now);
        return transaction;
    }
}
