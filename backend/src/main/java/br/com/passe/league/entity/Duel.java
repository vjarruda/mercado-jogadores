package br.com.passe.league.entity;

import br.com.passe.club.entity.Club;
import br.com.passe.player.entity.Player;
import br.com.passe.player.enums.PlayerCondition;
import br.com.passe.player.enums.Position;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "duelos_partida")
@Getter
@Setter
@NoArgsConstructor
public class Duel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "partida_id")
    private Match match;

    @Column(name = "ordem", nullable = false)
    private Integer order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "clube_atacante_id")
    private Club attackingClub;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "atacante_id")
    private Player attacker;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "defensor_id")
    private Player defender;

    @Column(name = "nome_atacante", nullable = false, length = 100)
    private String attackerName;

    @Column(name = "nome_defensor", nullable = false, length = 100)
    private String defenderName;

    @Enumerated(EnumType.STRING)
    @Column(name = "posicao_atacante", nullable = false, length = 3)
    private Position attackerPosition;

    @Enumerated(EnumType.STRING)
    @Column(name = "posicao_defensor", nullable = false, length = 3)
    private Position defenderPosition;

    @Enumerated(EnumType.STRING)
    @Column(name = "condicao_atacante", nullable = false, length = 20)
    private PlayerCondition attackerCondition;

    @Enumerated(EnumType.STRING)
    @Column(name = "condicao_defensor", nullable = false, length = 20)
    private PlayerCondition defenderCondition;

    @Column(name = "atributo_ataque", nullable = false, length = 10)
    private String attackAttribute;

    @Column(name = "atributo_defesa", nullable = false, length = 10)
    private String defenseAttribute;

    @Column(name = "valor_ataque", nullable = false)
    private Integer attackValue;

    @Column(name = "valor_defesa", nullable = false)
    private Integer defenseValue;

    @Column(name = "gol", nullable = false)
    private Boolean goal;
}