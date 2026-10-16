package br.com.passe.club.service;

import br.com.passe.club.dto.ClubCreateRequest;
import br.com.passe.club.dto.ClubDetailResponse;
import br.com.passe.club.dto.ClubResponse;
import br.com.passe.club.dto.SquadPlayerResponse;
import br.com.passe.club.entity.Club;
import br.com.passe.club.repository.ClubRepository;
import br.com.passe.market.dto.ContractSummary;
import br.com.passe.market.entity.Contract;
import br.com.passe.market.enums.ContractStatus;
import br.com.passe.market.repository.ContractRepository;
import br.com.passe.market.service.LoyaltyRules;
import br.com.passe.player.dto.PlayerResponse;
import br.com.passe.player.mapper.PlayerMapper;
import br.com.passe.player.repository.PlayerRepository;
import br.com.passe.shared.exception.BadRequestException;
import br.com.passe.shared.exception.ConflictException;
import br.com.passe.shared.exception.NotFoundException;
import br.com.passe.shared.util.IdParser;
import br.com.passe.shared.util.TextNormalizer;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClubService {

    private static final int MAX_CLUBS = 20;

    private final ClubRepository clubRepository;
    private final PlayerRepository playerRepository;
    private final ContractRepository contractRepository;
    private final PlayerMapper playerMapper;
    private final ClubRules rules;
    private final LoyaltyRules loyaltyRules;

    @Transactional
    public ClubResponse create(ClubCreateRequest request) {
        String name = rules.validateName(request.name());
        rules.validateAcronym(request.acronym(), name);

        if (clubRepository.existsByNameIgnoreCase(name)) {
            throw new ConflictException("A club with this name already exists.");
        }
        if (clubRepository.existsByAcronym(request.acronym())) {
            throw new ConflictException("A club with this acronym already exists.");
        }
        if (clubRepository.count() >= MAX_CLUBS) {
            throw new ConflictException("The league accepts at most 20 clubs.");
        }

        Club club = new Club();
        club.setName(name);
        club.setAcronym(request.acronym());
        return summary(clubRepository.save(club), List.of());
    }

    @Transactional(readOnly = true)
    public List<ClubResponse> list(String sort) {
        return clubRepository.findAll().stream()
                .map(club -> summary(club, squadOf(club.getId())))
                .sorted(comparator(sort))
                .toList();
    }

    @Transactional(readOnly = true)
    public ClubDetailResponse findById(String rawId) {
        Club club = findClub(rawId);
        List<PlayerResponse> squad = squadOf(club.getId());

        Map<Integer, Contract> contracts = contractRepository
                .findAllByClubIdAndStatus(club.getId(), ContractStatus.ATIVO).stream()
                .collect(Collectors.toMap(contract -> contract.getPlayer().getId(), Function.identity()));

        LocalDateTime now = LocalDateTime.now();
        List<SquadPlayerResponse> players = squad.stream()
                .sorted(SQUAD_ORDER)
                .map(player -> new SquadPlayerResponse(player, contractSummary(contracts.get(player.id()), now)))
                .toList();

        return new ClubDetailResponse(summary(club, squad), players);
    }

    public Club findClub(String rawId) {
        return clubRepository.findById(IdParser.parseOrNotFound(rawId, "Club"))
                .orElseThrow(() -> new NotFoundException("Club not found."));
    }

    private List<PlayerResponse> squadOf(Integer clubId) {
        return playerRepository.findAllByClubIdAndDeletedAtIsNull(clubId).stream()
                .map(playerMapper::toResponse)
                .toList();
    }

    private ClubResponse summary(Club club, List<PlayerResponse> squad) {
        BigDecimal squadValue = squad.stream()
                .map(PlayerResponse::marketValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new ClubResponse(club.getId(), club.getName(), club.getAcronym(),
                club.getCash(), squadValue, club.getCash().add(squadValue));
    }

    private ContractSummary contractSummary(Contract contract, LocalDateTime now) {
        if (contract == null) {
            return null;
        }
        int days = loyaltyRules.remainingDays(contract.getLoyaltyUntil(), now);
        return new ContractSummary(contract.getPurchaseValue(), contract.getLoyaltyUntil(),
                days, loyaltyRules.penaltyRate(days));
    }

    private static final Comparator<PlayerResponse> SQUAD_ORDER = Comparator
            .comparingInt((PlayerResponse p) -> p.position().ordinal())
            .thenComparing(p -> TextNormalizer.fold(p.name()))
            .thenComparing(PlayerResponse::id);

    private Comparator<ClubResponse> comparator(String sort) {
        Comparator<ClubResponse> byName = Comparator.comparing((ClubResponse c) -> TextNormalizer.fold(c.name()));
        Comparator<ClubResponse> main = switch (sort == null ? "name" : sort) {
            case "name" -> byName;
            case "net_worth_asc" -> Comparator.comparing(ClubResponse::netWorth);
            case "net_worth_desc" -> Comparator.comparing(ClubResponse::netWorth).reversed();
            default -> throw new BadRequestException("sort: invalid value.");
        };
        return main.thenComparing(byName).thenComparing(ClubResponse::id);
    }
}