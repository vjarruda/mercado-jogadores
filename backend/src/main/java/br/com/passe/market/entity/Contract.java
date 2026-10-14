package br.com.passe.market.entity;

import br.com.passe.club.entity.Club;
import br.com.passe.market.enums.ContractStatus;
import br.com.passe.player.entity.Player;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "contratos")
@Getter
@Setter
@NoArgsConstructor
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "jogador_id")
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "clube_id")
    private Club club;

    @Column(name = "valor_compra", nullable = false, precision = 6, scale = 2)
    private BigDecimal purchaseValue;

    @Column(name = "fidelidade_ate", nullable = false)
    private LocalDateTime loyaltyUntil;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false, length = 12)
    private ContractStatus status = ContractStatus.ATIVO;

    @Column(name = "iniciado_em", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "encerrado_em")
    private LocalDateTime endedAt;
}