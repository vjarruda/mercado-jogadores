package br.com.passe.player.mapper;

import br.com.passe.player.dto.PlayerCreateRequest;
import br.com.passe.player.dto.PlayerResponse;
import br.com.passe.player.entity.Player;
import br.com.passe.player.service.utils.MarketValueCalculator;
import br.com.passe.player.service.utils.OverallCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class PlayerMapper {

    private final OverallCalculator overallCalculator;
    private final MarketValueCalculator marketValueCalculator;

    public PlayerResponse toResponse(Player player) {
        return new PlayerResponse(
                player.getId(),
                player.getName(),
                player.getPosition(),
                player.getAgility(),
                player.getAge(),
                player.getCondition(),
                player.getBaseValue(),
                marketValueCalculator.calculate(
                        player.getBaseValue(), player.getCondition(), player.getAgility(), player.getAge()),
                player.getPace(),
                player.getShooting(),
                player.getPassing(),
                player.getDribbling(),
                player.getDefending(),
                player.getPhysical(),
                player.getPotential(),
                overallCalculator.calculate(player.getPosition(), player.attributes()),
                player.isFree() ? null : player.getClub().getId());
    }
    public Player toEntity(PlayerCreateRequest request, String cleanName, BigDecimal baseValue) {
        Player player = new Player();
        player.setName(cleanName);
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
        player.setBaseValue(baseValue);
        return player;
    }

}
