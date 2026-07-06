package app.pawnshop.contract.service;

import app.pawnshop.contract.dto.ContractRequest;
import app.pawnshop.contract.exception.ContractNotFoundException;
import app.pawnshop.contract.exception.InvalidContractStatusTransitionException;
import app.pawnshop.contract.model.Contract;
import app.pawnshop.contract.model.ContractStatus;
import app.pawnshop.contract.repository.ContractRepository;
import app.pawnshop.customer.model.Customer;
import app.pawnshop.customer.service.CustomerService;
import app.pawnshop.pawnitem.model.PawnItem;
import app.pawnshop.pawnitem.service.PawnItemService;
import app.pawnshop.user.exception.UserNotFoundException;
import app.pawnshop.user.model.User;
import app.pawnshop.user.repository.UserRepository;
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
    private final CustomerService customerService;
    private final PawnItemService pawnItemService;
    private final UserRepository userRepository;

    public Contract createContract(ContractRequest request) {
        Customer customer = customerService.getCustomerById(request.getCustomerId());
        PawnItem pawnItem = pawnItemService.getPawnItemById(request.getPawnItemId());
        User createdBy = userRepository.findById(request.getCreatedById())
                .orElseThrow(() -> new UserNotFoundException(request.getCreatedById()));

        Contract contract = Contract.builder()
                .contractNumber(request.getContractNumber())
                .loanAmount(request.getLoanAmount())
                .interestRate(request.getInterestRate())
                .startDate(request.getStartDate())
                .dueDate(request.getDueDate())
                .status(ContractStatus.ACTIVE)
                .customer(customer)
                .pawnItem(pawnItem)
                .createdBy(createdBy)
                .build();

        Contract saved = contractRepository.save(contract);
        log.info("Contract created with id: {}", saved.getId());
        return saved;
    }

    public Contract getContractById(UUID id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new ContractNotFoundException(id));
    }

    public List<Contract> getAllContracts() {
        return contractRepository.findAll();
    }

    public Contract updateContract(UUID id, ContractRequest request) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ContractNotFoundException(id));

        Customer customer = customerService.getCustomerById(request.getCustomerId());
        PawnItem pawnItem = pawnItemService.getPawnItemById(request.getPawnItemId());
        User createdBy = userRepository.findById(request.getCreatedById())
                .orElseThrow(() -> new UserNotFoundException(request.getCreatedById()));

        contract.setContractNumber(request.getContractNumber());
        contract.setLoanAmount(request.getLoanAmount());
        contract.setInterestRate(request.getInterestRate());
        contract.setStartDate(request.getStartDate());
        contract.setDueDate(request.getDueDate());
        contract.setCustomer(customer);
        contract.setPawnItem(pawnItem);
        contract.setCreatedBy(createdBy);

        Contract updated = contractRepository.save(contract);
        log.info("Contract updated with id: {}", updated.getId());
        return updated;
    }

    public void deleteContract(UUID id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ContractNotFoundException(id));

        validateStatusTransition(contract.getStatus(), ContractStatus.CANCELLED);
        contract.setStatus(ContractStatus.CANCELLED);
        contractRepository.save(contract);
        log.info("Contract cancelled with id: {}", id);
    }

    public Contract changeContractStatus(UUID id, ContractStatus newStatus) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ContractNotFoundException(id));

        validateStatusTransition(contract.getStatus(), newStatus);
        contract.setStatus(newStatus);
        Contract updated = contractRepository.save(contract);
        log.info("Contract {} status changed to {}", id, newStatus);
        return updated;
    }

    private void validateStatusTransition(ContractStatus current, ContractStatus next) {
        boolean valid = switch (current) {
            case ACTIVE -> next == ContractStatus.REDEEMED
                    || next == ContractStatus.EXPIRED
                    || next == ContractStatus.CANCELLED;
            case EXPIRED -> next == ContractStatus.SOLD;
            default -> false;
        };

        if (!valid) {
            throw new InvalidContractStatusTransitionException(current, next);
        }
    }
}
