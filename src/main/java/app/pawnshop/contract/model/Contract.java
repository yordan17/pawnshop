package app.pawnshop.contract.model;

import app.pawnshop.customer.model.Customer;
import app.pawnshop.pawnitem.model.PawnItem;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "contracts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String contractNumber;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal loanAmount;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractStatus status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pawn_item_id", nullable = false)
    private PawnItem pawnItem;

    @ManyToOne(optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;
}
