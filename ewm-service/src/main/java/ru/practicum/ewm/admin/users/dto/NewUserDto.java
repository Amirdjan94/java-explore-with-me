package ru.practicum.ewm.admin.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Component;

@Data
@Component
public class NewUserDto {

    @NotBlank
    @Email
    @Length(max = 254, min = 6)
    private String email; // адрес электронной почты
    @NotBlank
    @Length(max = 250, min = 2)
    private String name; // имя или логин пользователя

}
