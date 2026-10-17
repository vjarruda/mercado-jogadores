package br.com.passe.market.repository;

import br.com.passe.market.entity.MarketTransaction;
import br.com.passe.market.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<MarketTransaction, Integer> {

    boolean existsByClubIdAndPlayerIdAndType(Integer clubId, Integer playerId, TransactionType type);
}
