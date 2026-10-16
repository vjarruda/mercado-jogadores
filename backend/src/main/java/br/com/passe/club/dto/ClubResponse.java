package br.com.passe.club.dto;

import java.math.BigDecimal;

public record ClubResponse(
        Integer id,
        String name,
        String acronym,
        BigDecimal cash,
        BigDecimal squadValue,
        BigDecimal netWorth) {
}
