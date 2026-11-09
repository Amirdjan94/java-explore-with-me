package ru.practicum.ewm.privatePart.events.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.privatePart.requests.data.RequestStatus;

import java.util.Set;

@Data
@Component
public class EventRequestStatusUpdateRequestDto {

    @NotNull
    private Set<Long> requestIds;
    @NotNull
    private RequestStatus status;

}