package br.com.passe.player.dto;

import br.com.passe.player.enums.Position;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PlayerCreateRequest(
        @NotNull String name,
        @NotNull Position position,
        @NotNull @Min(1) @Max(100) Integer agility,
        @NotNull @Min(16) @Max(45) Integer age,
        @NotNull @Min(1) @Max(99) Integer pace,
        @NotNull @Min(1) @Max(99) Integer shooting,
        @NotNull @Min(1) @Max(99) Integer passing,
        @NotNull @Min(1) @Max(99) Integer dribbling,
        @NotNull @Min(1) @Max(99) Integer defending,
        @NotNull @Min(1) @Max(99) Integer physical,
        @NotNull @Min(1) @Max(99) Integer potential) {
}
