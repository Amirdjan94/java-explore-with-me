package ru.practicum.ewm.admin.category.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Component;

@Data
@Component
public class NewCategoryDto {
    @NotBlank
    @Length(max = 50, min = 2)
    private String name; // имя категории
}