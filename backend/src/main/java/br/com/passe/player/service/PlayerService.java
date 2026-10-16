package br.com.passe.player.service;

import br.com.passe.player.dto.ConditionRequest;
import br.com.passe.player.dto.PlayerCreateRequest;
import br.com.passe.player.dto.PlayerResponse;
import br.com.passe.player.dto.PlayerUpdateRequest;
import br.com.passe.player.entity.Player;
import br.com.passe.player.enums.PlayerCondition;
import br.com.passe.player.mapper.PlayerMapper;
import br.com.passe.player.model.Attributes;
import br.com.passe.player.repository.PlayerRepository;
import br.com.passe.player.service.utils.BaseValueCalculator;
import br.com.passe.player.service.utils.OverallCalculator;
import br.com.passe.player.service.utils.PlayerRulesValidator;
import br.com.passe.shared.exception.ConflictException;
import br.com.passe.shared.exception.NotFoundException;
import br.com.passe.shared.util.IdParser;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.passe.club.repository.ClubRepository;
import br.com.passe.player.dto.PlayerListQuery;
import br.com.passe.shared.dto.PageResponse;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerRulesValidator rulesValidator;
    private final OverallCalculator overallCalculator;
    private final BaseValueCalculator baseValueCalculator;
    private final PlayerMapper playerMapper;
    private final ClubRepository clubRepository;

    @Transactional
    public PlayerResponse create(PlayerCreateRequest request) {
        String name = rulesValidator.validateName(request.name());
        rulesValidator.validateRegistration(request);

        int overall = overallCalculator.calculate(request.position(), request.attributes());
        BigDecimal baseValue = baseValueCalculator.calculate(overall, request.potential(), request.age());

        Player player = playerMapper.toEntity(request, name, baseValue);
        return playerMapper.toResponse(playerRepository.save(player));
    }

    @Transactional(readOnly = true)
    public PlayerResponse findById(String rawId) {
        return playerMapper.toResponse(findActive(rawId));
    }

    @Transactional
    public PlayerResponse update(String rawId, PlayerUpdateRequest request) {
        Player player = findActive(rawId);

        String name = rulesValidator.validateName(request.name());
        rulesValidator.validateAgility(request.agility(), request.age());

        if (!player.isFree() && player.getPosition() != request.position()) {
            throw new ConflictException("A player that belongs to a club cannot change position.");
        }
        if (request.age() < player.getAge()) {
            throw new ConflictException("The age cannot be lower than the current age.");
        }

        player.setName(name);
        player.setPosition(request.position());
        player.setAgility(request.agility());
        player.setAge(request.age());

        return playerMapper.toResponse(playerRepository.save(player));
    }

    @Transactional
    public PlayerResponse updateCondition(String rawId, ConditionRequest request) {
        Player player = findActive(rawId);

        if (player.getCondition() == PlayerCondition.DOENTE && request.condition() == PlayerCondition.APTO) {
            throw new ConflictException("A sick player cannot go straight to APTO.");
        }

        player.setCondition(request.condition());
        return playerMapper.toResponse(playerRepository.save(player));
    }

    @Transactional
    public void delete(String rawId) {
        Player player = findActive(rawId);

        if (!player.isFree()) {
            throw new ConflictException("Only free players can be deleted.");
        }

        player.setDeletedAt(LocalDateTime.now());
        playerRepository.save(player);
    }

    //jogador deletado logicamente conta como nao existente
    public Player findActive(String rawId) {
        int id = IdParser.parseOrNotFound(rawId, "Player");
        return playerRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Player not found."));
    }

    @Transactional(readOnly = true)
    public PageResponse<PlayerResponse> list(PlayerListQuery query) {
        if (query.clubId() != null && !clubRepository.existsById(query.clubId())) {
            throw new NotFoundException("Club not found.");
        }

        List<PlayerResponse> filtered = playerRepository.findAllByDeletedAtIsNull().stream()
                .map(playerMapper::toResponse)
                .filter(query::matches)
                .sorted(query.comparator())
                .toList();

        return PageResponse.of(filtered, query.page(), query.limit());
    }
}