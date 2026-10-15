package br.com.passe.player.dto;

import br.com.passe.player.enums.PlayerCondition;
import jakarta.validation.constraints.NotNull;

public record ConditionRequest(@NotNull PlayerCondition condition) {
}
