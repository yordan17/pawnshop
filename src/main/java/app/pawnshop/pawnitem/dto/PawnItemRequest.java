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

    @NotBlank(message = "Наименованието е задължително")
    @Size(min = 2, max = 100, message = "Наименованието трябва да е между 2 и 100 символа")
    private String name;

    @Size(max = 500, message = "Описанието не може да надвишава 500 символа")
    private String description;

    @NotNull(message = "Категорията е задължителна")
    private ItemCategory category;

    @NotNull(message = "Състоянието е задължително")
    private ItemCondition condition;

    @NotNull(message = "Оценената стойност е задължителна")
    @DecimalMin(value = "0.01", message = "Оценената стойност трябва да е поне 0.01")
    private BigDecimal estimatedValue;

    @NotNull(message = "Лихвеният процент е задължителен")
    @DecimalMin(value = "0.01", message = "Лихвеният процент трябва да е поне 0.01")
    private BigDecimal interestRate;

    @NotNull(message = "Клиентът е задължителен")
    private UUID customerId;
}
