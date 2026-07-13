package app.pawnshop.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerRequest {

    @NotBlank(message = "Името е задължително")
    @Size(min = 2, max = 50, message = "Името трябва да е между 2 и 50 символа")
    private String firstName;

    @NotBlank(message = "Фамилията е задължителна")
    @Size(min = 2, max = 50, message = "Фамилията трябва да е между 2 и 50 символа")
    private String lastName;

    @NotBlank(message = "Телефонът е задължителен")
    @Pattern(regexp = "\\d{5,20}", message = "Телефонът трябва да съдържа само цифри (5-20)")
    private String phoneNumber;

    @NotBlank(message = "Имейлът е задължителен")
    @Email(message = "Невалиден имейл адрес")
    private String email;

    @NotBlank(message = "ЕГН-то е задължително")
    @Pattern(regexp = "\\d{10}", message = "ЕГН трябва да съдържа точно 10 цифри")
    private String personalId;

    @NotBlank(message = "Градът е задължителен")
    @Size(max = 100, message = "Градът не може да надвишава 100 символа")
    private String city;

    @NotBlank(message = "Улицата е задължителна")
    @Size(max = 255, message = "Улицата не може да надвишава 255 символа")
    private String street;
}
