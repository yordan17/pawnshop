package app.pawnshop.contract.service;

import app.pawnshop.contract.dto.ContractRequest;
import app.pawnshop.contract.exception.ContractNotFoundException;
import app.pawnshop.contract.exception.InvalidContractStatusTransitionException;
import app.pawnshop.contract.model.Contract;
import app.pawnshop.contract.model.ContractStatus;
import app.pawnshop.contract.repository.ContractRepository;
import app.pawnshop.customer.model.Customer;
import app.pawnshop.pawnitem.model.ItemStatus;
import app.pawnshop.pawnitem.model.PawnItem;
import app.pawnshop.pawnitem.service.PawnItemService;
import app.pawnshop.user.model.User;
import app.pawnshop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;
    private final PawnItemService pawnItemService;
    private final UserService userService;

    public Contract createContract(ContractRequest request, String username) {
        PawnItem pawnItem = pawnItemService.getPawnItemById(request.getPawnItemId());
        Customer customer = pawnItem.getCustomer();
        User createdBy = userService.getUserByUsername(username);
        String contractNumber = String.format("C-%04d", contractRepository.count() + 1);

        Contract contract = Contract.builder()
                .contractNumber(contractNumber)
                .loanAmount(pawnItem.getEstimatedValue())
                .interestRate(pawnItem.getInterestRate())
                .startDate(request.getStartDate())
                .dueDate(request.getDueDate())
                .status(ContractStatus.ACTIVE)
                .customer(customer)
                .pawnItem(pawnItem)
                .createdBy(createdBy)
                .build();

        Contract saved = contractRepository.save(contract);
        pawnItemService.changeItemStatus(pawnItem.getId(), ItemStatus.PAWNED);
        log.info("Contract {} created with id: {}", contractNumber, saved.getId());
        return saved;
    }

    public Contract getContractById(UUID id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new ContractNotFoundException(id));
    }

    public List<Contract> getAllContracts() {
        return contractRepository.findAll();
    }

    public List<Contract> getContractsByStatus(ContractStatus status) {
        return contractRepository.findByStatus(status);
    }

    public Contract updateContract(UUID id, ContractRequest request) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ContractNotFoundException(id));

        contract.setStartDate(request.getStartDate());
        contract.setDueDate(request.getDueDate());

        Contract updated = contractRepository.save(contract);
        log.info("Contract updated with id: {}", updated.getId());
        return updated;
    }

    public void deleteContract(UUID id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ContractNotFoundException(id));

        if (contract.getStatus() == ContractStatus.ACTIVE) {
            pawnItemService.changeItemStatus(contract.getPawnItem().getId(), ItemStatus.AVAILABLE);
        }

        contractRepository.deleteById(id);
        log.info("Contract {} deleted with id: {}", contract.getContractNumber(), id);
    }

    public Contract changeContractStatus(UUID id, ContractStatus newStatus) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ContractNotFoundException(id));

        validateStatusTransition(contract.getStatus(), newStatus);
        contract.setStatus(newStatus);
        Contract updated = contractRepository.save(contract);

        if (newStatus == ContractStatus.REDEEMED) {
            pawnItemService.changeItemStatus(contract.getPawnItem().getId(), ItemStatus.AVAILABLE);
        }

        log.info("Contract {} status changed to {}", id, newStatus);
        return updated;
    }

    private void validateStatusTransition(ContractStatus current, ContractStatus next) {
        boolean valid = switch (current) {
            case ACTIVE -> next == ContractStatus.REDEEMED
                    || next == ContractStatus.EXPIRED;
            case EXPIRED -> next == ContractStatus.SOLD;
            default -> false;
        };

        if (!valid) {
            throw new InvalidContractStatusTransitionException(current, next);
        }
    }
}
