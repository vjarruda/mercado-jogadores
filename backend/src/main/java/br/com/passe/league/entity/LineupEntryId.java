package br.com.passe.league.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LineupEntryId implements Serializable {

    @Column(name = "clube_id")
    private Integer clubId;

    @Column(name = "jogador_id")
    private Integer playerId;
}