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
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerRulesValidator rulesValidator;
    private final OverallCalculator overallCalculator;
    private final BaseValueCalculator baseValueCalculator;
    private final PlayerMapper playerMapper;

    @Transactional
    public PlayerResponse create(PlayerCreateRequest request) {
        String name = rulesValidator.validateName(request.name());
        rulesValidator.validateRegistration(request);

        Player player = new Player();
        player.setName(name);
        player.setPosition(request.position());
        player.setAgility(request.agility());
        player.setAge(request.age());
        player.setPace(request.pace());
        player.setShooting(request.shooting());
        player.setPassing(request.passing());
        player.setDribbling(request.dribbling());
        player.setDefending(request.defending());
        player.setPhysical(request.physical());
        player.setPotential(request.potential());

        // valor base é calculado uma vez e nao muda
        Attributes attributes = player.attributes();
        int overall = overallCalculator.calculate(request.position(), attributes);
        player.setBaseValue(baseValueCalculator.calculate(overall, request.potential(), request.age()));

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
}