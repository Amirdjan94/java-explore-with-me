package ru.practicum.ewm.admin.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.admin.events.data.AdminStateAction;
import ru.practicum.ewm.privatePart.events.model.Location;

import java.time.LocalDateTime;

@Data
@Component
public class AdminUpdateEventRequestDto {

    @Length(max = 7000, min = 20)
    private String description;

    @Length(max = 2000, min = 20)
    private String annotation;

    @Min(1)
    private Long category;

    private Location location;

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

    @Length(max = 120, min = 3)
    private String title;

    private AdminStateAction stateAction;
}