package app.pawnshop.payment.repository;

import app.pawnshop.payment.model.Payment;
import app.pawnshop.payment.model.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findByContractId(UUID contractId);

    List<Payment> findByType(PaymentType type);

    List<Payment> findByReceivedById(UUID userId);
}
