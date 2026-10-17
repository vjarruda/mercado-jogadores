package br.com.passe.market.service;

import br.com.passe.club.entity.Club;
import br.com.passe.club.repository.ClubRepository;
import br.com.passe.market.dto.BuyRequest;
import br.com.passe.market.dto.BuyResponse;
import br.com.passe.market.dto.TransactionResponse;
import br.com.passe.market.entity.Contract;
import br.com.passe.market.entity.MarketTransaction;
import br.com.passe.market.enums.TransactionType;
import br.com.passe.market.repository.ContractRepository;
import br.com.passe.market.repository.TransactionRepository;
import br.com.passe.player.entity.Player;
import br.com.passe.player.repository.PlayerRepository;
import br.com.passe.player.service.PlayerService;
import br.com.passe.player.service.utils.MarketValueCalculator;
import br.com.passe.shared.exception.BadRequestException;
import br.com.passe.shared.exception.ConflictException;
import br.com.passe.shared.exception.NotFoundException;
import br.com.passe.shared.util.BrasiliaDateTime;
import br.com.passe.shared.util.Money;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private static final int MAX_SQUAD = 12;
    private static final long MAX_LOYALTY_DAYS = 1095;
    private static final BigDecimal FEE_THRESHOLD = new BigDecimal("30.00");
    private static final BigDecimal FEE_RATE = new BigDecimal("0.05");

    private final PlayerService playerService;
    private final PlayerRepository playerRepository;
    private final ClubRepository clubRepository;
    private final ContractRepository contractRepository;
    private final TransactionRepository transactionRepository;
    private final MarketValueCalculator marketValueCalculator;

    @Transactional
    public BuyResponse buy(String rawPlayerId, BuyRequest request) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime loyaltyUntil = parseLoyalty(request.loyaltyUntil(), now);
        Player player = playerService.findActive(rawPlayerId);
        Club club = clubRepository.findById(request.clubId())
                .orElseThrow(() -> new NotFoundException("Club not found."));

        BigDecimal value = marketValueCalculator.calculate(
                player.getBaseValue(), player.getCondition(), player.getAgility(), player.getAge());
        BigDecimal fee = fee(value);
        BigDecimal totalCost = value.add(fee);
        ensureCanBuy(player, club, totalCost);

        club.setCash(club.getCash().subtract(totalCost));
        player.setClub(club);
        Contract contract = contractRepository.save(newContract(player, club, value, loyaltyUntil, now));
        MarketTransaction transaction = transactionRepository.save(newTransaction(player, club, value, fee, now));

        return BuyResponse.of(TransactionResponse.from(transaction), contract, club.getCash());
    }

    private LocalDateTime parseLoyalty(String raw, LocalDateTime now) {
        LocalDateTime loyaltyUntil = BrasiliaDateTime.parse("loyalty_until", raw);
        if (!loyaltyUntil.isAfter(now) || loyaltyUntil.isAfter(now.plusDays(MAX_LOYALTY_DAYS))) {
            throw new BadRequestException("loyalty_until: must be in the future and at most 1095 days ahead.");
        }
        return loyaltyUntil;
    }

    private BigDecimal fee(BigDecimal value) {
        return value.compareTo(FEE_THRESHOLD) > 0 ? Money.round(value.multiply(FEE_RATE)) : Money.round(BigDecimal.ZERO);
    }

    private void ensureCanBuy(Player player, Club club, BigDecimal totalCost) {
        if (!player.isFree()) {
            throw new ConflictException("The player is not free.");
        }
        if (transactionRepository.existsByClubIdAndPlayerIdAndType(club.getId(), player.getId(), TransactionType.VENDA)) {
            throw new ConflictException("A club cannot buy back a player it has sold.");
        }
        if (playerRepository.countByClubIdAndDeletedAtIsNull(club.getId()) >= MAX_SQUAD) {
            throw new ConflictException("The club already has 12 players.");
        }
        if (totalCost.multiply(BigDecimal.valueOf(2)).compareTo(club.getCash()) > 0) {
            throw new ConflictException("The purchase cost passes the limit of 50% of the club cash.");
        }
    }

    private Contract newContract(Player player, Club club, BigDecimal value, LocalDateTime loyaltyUntil,
                                 LocalDateTime now) {
        Contract contract = new Contract();
        contract.setPlayer(player);
        contract.setClub(club);
        contract.setPurchaseValue(value);
        contract.setLoyaltyUntil(loyaltyUntil);
        contract.setStartedAt(now);
        return contract;
    }

    private MarketTransaction newTransaction(Player player, Club club, BigDecimal value, BigDecimal fee,
                                             LocalDateTime now) {
        MarketTransaction transaction = new MarketTransaction();
        transaction.setClub(club);
        transaction.setPlayer(player);
        transaction.setPlayerName(player.getName());
        transaction.setType(TransactionType.COMPRA);
        transaction.setValue(value);
        transaction.setFee(fee);
        transaction.setCashChange(value.add(fee).negate());
        transaction.setCreatedAt(now);
        return transaction;
    }
}
