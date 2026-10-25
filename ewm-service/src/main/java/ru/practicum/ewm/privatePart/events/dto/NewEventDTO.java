package ru.practicum.ewm.privatePart.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.privatePart.events.model.Location;

import java.time.LocalDateTime;

@Data
@Component
public class NewEventDTO {

    @NotBlank
    @Length(max = 7000, min = 20)
    private String description;

    @NotBlank
    @Length(max = 2000, min = 20)
    private String annotation;

    @NotNull
    @Min(1)
    private Long category;

    @NotNull
    private Location location;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Future
    private LocalDateTime eventDate;

    @JsonProperty(defaultValue = "false")
    private Boolean paid;

    @JsonProperty(defaultValue = "0")
    @Min(1)
    private Integer participantLimit;

    @JsonProperty(defaultValue = "true")
    private Boolean requestModeration;

    @NotBlank
    @Length(max = 120, min = 3)
    private String title;

}