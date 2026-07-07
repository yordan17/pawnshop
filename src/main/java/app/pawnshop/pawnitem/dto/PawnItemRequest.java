package app.pawnshop.pawnitem.dto;

import app.pawnshop.pawnitem.model.ItemCategory;
import app.pawnshop.pawnitem.model.ItemCondition;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class PawnItemRequest {

    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    @NotNull
    private ItemCategory category;

    @NotNull
    private ItemCondition condition;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal estimatedValue;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal interestRate;

    @NotNull
    private UUID customerId;
}
