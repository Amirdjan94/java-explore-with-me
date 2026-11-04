package ru.practicum.ewm.admin.compilations.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Data
public class UpdateCompilationDto {

    private Set<Long> events;
    private Boolean pinned;
    @Length(max = 50, min = 1)
    private String title;

}