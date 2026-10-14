package br.com.passe.league.entity;

import br.com.passe.club.entity.Club;
import br.com.passe.club.enums.Formation;
import br.com.passe.club.converter.FormationConverter;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "partidas")
@Getter
@Setter
@NoArgsConstructor
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mandante_id")
    private Club homeClub;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "visitante_id")
    private Club awayClub;

    @Column(name = "gols_mandante", nullable = false)
    private Integer homeGoals;

    @Column(name = "gols_visitante", nullable = false)
    private Integer awayGoals;

    @Convert(converter = FormationConverter.class)
    @Column(name = "formacao_mandante", nullable = false, length = 5)
    private Formation homeFormation;

    @Convert(converter = FormationConverter.class)
    @Column(name = "formacao_visitante", nullable = false, length = 5)
    private Formation awayFormation;

    @Column(name = "jogada_em", nullable = false)
    private LocalDateTime playedAt;
}