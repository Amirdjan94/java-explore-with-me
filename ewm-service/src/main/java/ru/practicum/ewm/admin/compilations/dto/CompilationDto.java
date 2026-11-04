package ru.practicum.ewm.admin.compilations.dto;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.privatePart.events.dto.EventShortDTO;

import java.util.Set;

@Component
@Data
public class CompilationDto {

    private Long id;
    private Set<EventShortDTO> events;
    private Boolean pinned;
    private String title;

}
