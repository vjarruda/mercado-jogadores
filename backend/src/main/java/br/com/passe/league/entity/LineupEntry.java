package br.com.passe.league.entity;

import br.com.passe.club.entity.Club;
import br.com.passe.player.entity.Player;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "escalacoes")
@Getter
@Setter
@NoArgsConstructor
public class LineupEntry {

    @EmbeddedId
    private LineupEntryId id;

    @MapsId("clubId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "clube_id")
    private Club club;

    @MapsId("playerId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "jogador_id")
    private Player player;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public LineupEntry(Club club, Player player) {
        this.id = new LineupEntryId(club.getId(), player.getId());
        this.club = club;
        this.player = player;
    }
}