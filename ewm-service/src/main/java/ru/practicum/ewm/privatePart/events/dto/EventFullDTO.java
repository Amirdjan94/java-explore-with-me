package ru.practicum.ewm.privatePart.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.admin.category.model.Category;
import ru.practicum.ewm.admin.users.dto.UserShortDto;
import ru.practicum.ewm.privatePart.events.data.State;
import ru.practicum.ewm.privatePart.events.model.Location;

import java.time.LocalDateTime;

@Data
@Component
public class EventFullDTO {

    private Long id;
    private String annotation;
    private Long confirmedRequests;
    private String description;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private State state;
    private String title;
    private Long views;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;
    private UserShortDto initiator;
    private Category category;
    private Location location;

}