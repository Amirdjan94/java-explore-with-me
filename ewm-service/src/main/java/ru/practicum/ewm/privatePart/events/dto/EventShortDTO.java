package ru.practicum.ewm.privatePart.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.admin.category.model.Category;
import ru.practicum.ewm.admin.users.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@Component
public class EventShortDTO {

    private Long id;
    private String annotation;
    private Long confirmedRequests;
    private Boolean paid;
    private String title;
    private Long views;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private Category category;

}