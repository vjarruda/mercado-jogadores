package br.com.passe.market.repository;

import br.com.passe.market.entity.Contract;
import br.com.passe.market.enums.ContractStatus;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, Integer> {

    List<Contract> findAllByClubIdAndStatus(Integer clubId, ContractStatus status);

    Optional<Contract> findByPlayerIdAndStatus(Integer playerId, ContractStatus status);
}
