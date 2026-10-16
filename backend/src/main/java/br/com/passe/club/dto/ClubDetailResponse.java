package br.com.passe.club.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.util.List;

public record ClubDetailResponse(@JsonUnwrapped ClubResponse club, List<SquadPlayerResponse> players) {
}
