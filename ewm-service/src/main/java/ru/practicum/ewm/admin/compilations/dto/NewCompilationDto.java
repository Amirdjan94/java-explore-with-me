package ru.practicum.ewm.admin.compilations.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Data
public class NewCompilationDto {

    private Set<Long> events;
    @JsonProperty(defaultValue = "false")
    private Boolean pinned = false;
    @NotBlank
    @Length(max = 50, min = 1)
    private String title;

}
