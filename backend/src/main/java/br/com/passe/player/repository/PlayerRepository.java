package br.com.passe.player.repository;

import br.com.passe.player.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Integer> {

    Optional<Player> findByIdAndDeletedAtIsNull(Integer id);

    List<Player> findAllByDeletedAtIsNull();
}
