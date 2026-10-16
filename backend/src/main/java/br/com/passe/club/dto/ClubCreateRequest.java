package br.com.passe.club.dto;

import jakarta.validation.constraints.NotNull;

public record ClubCreateRequest(@NotNull String name, @NotNull String acronym) {
}
