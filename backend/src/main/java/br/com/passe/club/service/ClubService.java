package br.com.passe.club.service;

import br.com.passe.club.dto.ClubCreateRequest;
import br.com.passe.club.dto.ClubResponse;
import br.com.passe.club.entity.Club;
import br.com.passe.club.repository.ClubRepository;
import br.com.passe.player.dto.PlayerResponse;
import br.com.passe.player.mapper.PlayerMapper;
import br.com.passe.player.repository.PlayerRepository;
import br.com.passe.shared.exception.BadRequestException;
import br.com.passe.shared.exception.ConflictException;
import br.com.passe.shared.util.TextNormalizer;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClubService {

    private static final int MAX_CLUBS = 20;

    private final ClubRepository clubRepository;
    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;
    private final ClubRules rules;

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
        return toResponse(clubRepository.save(club));
    }

    @Transactional(readOnly = true)
    public List<ClubResponse> list(String sort) {
        return clubRepository.findAll().stream()
                .map(this::toResponse)
                .sorted(comparator(sort))
                .toList();
    }

    ClubResponse toResponse(Club club) {
        BigDecimal squadValue = playerRepository.findAllByClubIdAndDeletedAtIsNull(club.getId()).stream()
                .map(playerMapper::toResponse)
                .map(PlayerResponse::marketValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new ClubResponse(club.getId(), club.getName(), club.getAcronym(),
                club.getCash(), squadValue, club.getCash().add(squadValue));
    }

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
