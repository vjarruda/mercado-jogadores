package br.com.passe.club.dto;

import br.com.passe.market.dto.ContractSummary;
import br.com.passe.player.dto.PlayerResponse;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public record SquadPlayerResponse(@JsonUnwrapped PlayerResponse player, ContractSummary contract) {
}
