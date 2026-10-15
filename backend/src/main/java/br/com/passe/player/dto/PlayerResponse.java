package br.com.passe.player.dto;

import br.com.passe.player.enums.PlayerCondition;
import br.com.passe.player.enums.Position;

import java.math.BigDecimal;

public record PlayerResponse(
        Integer id,
        String name,
        Position position,
        Integer agility,
        Integer age,
        PlayerCondition condition,
        BigDecimal baseValue,
        BigDecimal marketValue,
        Integer pace,
        Integer shooting,
        Integer passing,
        Integer dribbling,
        Integer defending,
        Integer physical,
        Integer potential,
        Integer overall,
        Integer clubId) {
}