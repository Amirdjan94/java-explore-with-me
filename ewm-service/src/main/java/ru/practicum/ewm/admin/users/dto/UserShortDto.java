package ru.practicum.ewm.admin.users.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class UserShortDto {

    private Long id;
    private String name; // имя или логин пользователя
}
