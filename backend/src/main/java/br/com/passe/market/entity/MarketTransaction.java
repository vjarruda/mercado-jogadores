package br.com.passe.market.entity;

import br.com.passe.club.entity.Club;
import br.com.passe.market.enums.TransactionType;
import br.com.passe.player.entity.Player;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "transacoes")
@Getter
@Setter
@NoArgsConstructor
public class MarketTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "clube_id")
    private Club club;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "jogador_id")
    private Player player;

    @Column(name = "nome_jogador", nullable = false, length = 100)
    private String playerName;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 6)
    private TransactionType type;

    @Column(name = "valor", nullable = false, precision = 6, scale = 2)
    private BigDecimal value;

    @Column(name = "taxa", nullable = false, precision = 6, scale = 2)
    private BigDecimal fee;

    @Column(name = "multa", nullable = false, precision = 6, scale = 2)
    private BigDecimal penalty = BigDecimal.ZERO;

    @Column(name = "lucro", precision = 7, scale = 2)
    private BigDecimal profit;

    @Column(name = "variacao_caixa", nullable = false, precision = 8, scale = 2)
    private BigDecimal cashChange;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime createdAt;
}