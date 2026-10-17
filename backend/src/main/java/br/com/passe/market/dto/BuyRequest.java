package br.com.passe.market.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BuyRequest(@NotNull @Min(1) Integer clubId, @NotNull String loyaltyUntil) {
}
