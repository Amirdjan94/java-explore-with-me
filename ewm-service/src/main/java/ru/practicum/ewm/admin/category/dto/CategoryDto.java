package ru.practicum.ewm.admin.category.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class CategoryDto {

    private Long id; // уникальный идентификатор категории
    private String name; // имя категории
}
