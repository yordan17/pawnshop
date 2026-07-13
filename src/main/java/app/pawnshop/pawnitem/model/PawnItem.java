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
    @Size(min = 2, max = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    @Size(max = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_condition", nullable = false)
    private ItemCondition condition;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal estimatedValue;

    @Column(precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(precision = 8, scale = 2)
    private BigDecimal weightGrams;

    @Column
    private Integer purityCarats;

    @Column(length = 100)
    private String brand;

    @Column(length = 100)
    private String model;

    @Column(length = 100)
    private String serialNumber;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private JewelryType jewelryType;

    @Column
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemStatus status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private boolean active;
}
