package app.pawnshop.payment.model;

import app.pawnshop.contract.model.Contract;
import app.pawnshop.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    @Column(nullable = false)
    @NotNull
    private LocalDate paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private PaymentType type;

    @Column(length = 500)
    @Size(max = 500)
    private String notes;

    @ManyToOne(optional = false)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @ManyToOne(optional = false)
    @JoinColumn(name = "received_by", nullable = false)
    private User receivedBy;
}
