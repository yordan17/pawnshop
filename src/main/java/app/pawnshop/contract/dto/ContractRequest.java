package app.pawnshop.contract.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class ContractRequest {

    @NotBlank
    private String contractNumber;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal loanAmount;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal interestRate;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate dueDate;

    @NotNull
    private UUID customerId;

    @NotNull
    private UUID pawnItemId;

    @NotNull
    private UUID createdById;
}
