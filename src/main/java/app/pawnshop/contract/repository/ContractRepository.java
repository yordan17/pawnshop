package app.pawnshop.contract.repository;

import app.pawnshop.contract.model.Contract;
import app.pawnshop.contract.model.ContractStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContractRepository extends JpaRepository<Contract, UUID> {

    Optional<Contract> findByContractNumber(String contractNumber);

    List<Contract> findByCustomerId(UUID customerId);

    List<Contract> findByStatus(ContractStatus status);

    List<Contract> findByDueDateBeforeAndStatus(LocalDate date, ContractStatus status);
}
