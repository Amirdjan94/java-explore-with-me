package ru.practicum.ewm.privatePart.events.dto;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.privatePart.requests.dto.ParticipationRequestDto;

import java.util.List;

@Data
@Component
public class EventRequestStatusUpdateResultDto {
    private List<ParticipationRequestDto> confirmedRequests;
    private List<ParticipationRequestDto> rejectedRequests;
}