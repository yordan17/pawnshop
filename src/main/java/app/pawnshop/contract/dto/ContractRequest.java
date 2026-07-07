package app.pawnshop.contract.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class ContractRequest {

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate dueDate;

    @NotNull
    private UUID pawnItemId;
}
