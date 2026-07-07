package app.pawnshop.payment.service;

import app.pawnshop.contract.model.Contract;
import app.pawnshop.contract.model.ContractStatus;
import app.pawnshop.contract.service.ContractService;
import app.pawnshop.payment.dto.PaymentRequest;
import app.pawnshop.payment.exception.ContractNotActiveException;
import app.pawnshop.payment.exception.PaymentNotFoundException;
import app.pawnshop.payment.model.Payment;
import app.pawnshop.payment.model.PaymentType;
import app.pawnshop.payment.repository.PaymentRepository;
import app.pawnshop.pawnitem.model.ItemStatus;
import app.pawnshop.pawnitem.service.PawnItemService;
import app.pawnshop.user.exception.UserNotFoundException;
import app.pawnshop.user.model.User;
import app.pawnshop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ContractService contractService;
    private final PawnItemService pawnItemService;
    private final UserRepository userRepository;

    public Payment createPayment(PaymentRequest request) {
        Contract contract = contractService.getContractById(request.getContractId());

        if (contract.getStatus() != ContractStatus.ACTIVE) {
            throw new ContractNotActiveException(contract.getId(), contract.getStatus());
        }

        User receivedBy = userRepository.findById(request.getReceivedById())
                .orElseThrow(() -> new UserNotFoundException(request.getReceivedById()));

        Payment payment = Payment.builder()
                .amount(request.getAmount())
                .paymentDate(request.getPaymentDate())
                .type(request.getType())
                .notes(request.getNotes())
                .contract(contract)
                .receivedBy(receivedBy)
                .build();

        Payment saved = paymentRepository.save(payment);
        log.info("Payment created with id: {} for contract: {}", saved.getId(), contract.getId());

        if (request.getType() == PaymentType.REDEMPTION) {
            contractService.changeContractStatus(contract.getId(), ContractStatus.REDEEMED);
            pawnItemService.changeItemStatus(contract.getPawnItem().getId(), ItemStatus.AVAILABLE);
            log.info("Contract {} marked as REDEEMED, pawn item {} returned to AVAILABLE", contract.getId(), contract.getPawnItem().getId());
        } else if (request.getType() == PaymentType.PARTIAL) {
            BigDecimal accruedInterest = contractService.calculateAccruedInterestForDate(contract, request.getPaymentDate());
            BigDecimal principalReduction = request.getAmount().subtract(accruedInterest);
            if (principalReduction.compareTo(BigDecimal.ZERO) > 0) {
                contractService.reduceLoanAmount(contract.getId(), principalReduction);
                log.info("Contract {} principal reduced by {}, interest portion {}", contract.getId(), principalReduction, accruedInterest);
            } else {
                log.info("Contract {} partial payment covers only interest: {}", contract.getId(), request.getAmount());
            }
            contractService.extendDueDate(contract.getId());
            log.info("Contract {} due date extended by 1 month after partial payment", contract.getId());
        } else if (request.getType() == PaymentType.INTEREST) {
            contractService.extendDueDate(contract.getId());
            log.info("Contract {} due date extended by 1 month after interest payment", contract.getId());
        }

        return saved;
    }

    public Payment getPaymentById(UUID id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public List<Payment> getPaymentsByContractId(UUID contractId) {
        return paymentRepository.findByContractId(contractId);
    }
}
