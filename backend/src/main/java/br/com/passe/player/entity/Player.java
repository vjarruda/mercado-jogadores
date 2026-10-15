package br.com.passe.player.entity;

import br.com.passe.club.entity.Club;
import br.com.passe.player.enums.PlayerCondition;
import br.com.passe.player.enums.Position;
import br.com.passe.player.model.Attributes;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "jogadores")
@Getter
@Setter
@NoArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nome", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "posicao", nullable = false, length = 3)
    private Position position;

    @Column(name = "agilidade", nullable = false)
    private Integer agility;

    @Column(name = "idade", nullable = false)
    private Integer age;

    @Column(name = "valor_base", nullable = false, precision = 6, scale = 2)
    private BigDecimal baseValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "condicao", nullable = false, length = 20)
    private PlayerCondition condition = PlayerCondition.APTO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clube_id")
    private Club club;

    @Column(name = "velocidade", nullable = false)
    private Integer pace;

    @Column(name = "finalizacao", nullable = false)
    private Integer shooting;

    @Column(name = "passe", nullable = false)
    private Integer passing;

    @Column(name = "drible", nullable = false)
    private Integer dribbling;

    @Column(name = "defesa", nullable = false)
    private Integer defending;

    @Column(name = "fisico", nullable = false)
    private Integer physical;

    @Column(name = "potencial", nullable = false)
    private Integer potential;

    @Column(name = "excluido_em")
    private LocalDateTime deletedAt;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime updatedAt;

    public boolean isFree() {
        return club == null;
    }

    public Attributes attributes() {
        return new Attributes(pace, shooting, passing, dribbling, defending, physical);
    }
}