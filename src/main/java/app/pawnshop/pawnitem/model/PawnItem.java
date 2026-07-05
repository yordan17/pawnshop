package app.pawnshop.pawnitem.model;

import app.pawnshop.customer.model.Customer;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "pawn_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PawnItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    @Size(max = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private ItemCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_condition", nullable = false)
    @NotNull
    private ItemCondition condition;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal estimatedValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private ItemStatus status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private boolean active;
}
