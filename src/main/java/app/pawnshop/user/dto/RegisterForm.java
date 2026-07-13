package app.pawnshop.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterForm {

    @NotBlank(message = "Потребителското име е задължително")
    @Size(min = 3, max = 30, message = "Потребителското име трябва да е между 3 и 30 символа")
    private String username;

    @NotBlank(message = "Паролата е задължителна")
    @Size(min = 6, max = 100, message = "Паролата трябва да е поне 6 символа")
    private String password;

    @NotBlank(message = "Имейлът е задължителен")
    @Email(message = "Невалиден имейл адрес")
    private String email;
}
