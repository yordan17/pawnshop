package app.pawnshop.contract.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class ContractRequest {

    @NotNull(message = "Началната дата е задължителна")
    private LocalDate startDate;

    @NotNull(message = "Крайният срок е задължителен")
    private LocalDate dueDate;

    @NotNull(message = "Вещта е задължителна")
    private UUID pawnItemId;
}
