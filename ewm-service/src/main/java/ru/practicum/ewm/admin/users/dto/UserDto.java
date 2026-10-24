package ru.practicum.ewm.admin.users.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class UserDto {

    private Long id;
    private String email; // адрес электронной почты
    private String name; // имя или логин пользователя
}
