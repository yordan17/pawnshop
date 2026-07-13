package app.pawnshop.payment.dto;

import app.pawnshop.payment.model.PaymentType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class PaymentRequest {

    @NotNull(message = "Сумата е задължителна")
    @DecimalMin(value = "0.01", message = "Сумата трябва да е поне 0.01")
    private BigDecimal amount;

    @NotNull(message = "Датата е задължителна")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate paymentDate;

    @NotNull(message = "Типът на плащането е задължителен")
    private PaymentType type;

    @Size(max = 500, message = "Бележките не могат да надвишават 500 символа")
    private String notes;

    @NotNull(message = "Договорът е задължителен")
    private UUID contractId;

    private UUID receivedById;
}
