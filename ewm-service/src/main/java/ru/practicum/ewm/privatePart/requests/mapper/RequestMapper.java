package ru.practicum.ewm.privatePart.requests.mapper;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.admin.users.model.User;
import ru.practicum.ewm.privatePart.events.dto.EventRequestStatusUpdateResultDto;
import ru.practicum.ewm.privatePart.events.model.Event;
import ru.practicum.ewm.privatePart.requests.data.RequestStatus;
import ru.practicum.ewm.privatePart.requests.dto.ParticipationRequestDto;
import ru.practicum.ewm.privatePart.requests.model.Request;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor
public class RequestMapper {
    public static Request toRequestFromUserAndEvent(User user, Event event) {
        Request request = new Request();
        request.setUser(user);
        request.setEvent(event);
        request.setCreated(LocalDateTime.now());
        if (event.getRequestModeration() && event.getParticipantLimit() != 0) {
            request.setRequestStatus(RequestStatus.PENDING);
        } else {
            request.setRequestStatus(RequestStatus.CONFIRMED);
        }
        return request;
    }

    public static ParticipationRequestDto toParticipationRequestDtoFromRequestEntity(Request request) {
        ParticipationRequestDto participationRequestDto = new ParticipationRequestDto();
        participationRequestDto.setId(request.getId());
        participationRequestDto.setStatus(request.getRequestStatus());
        participationRequestDto.setEvent(request.getEvent().getId());
        participationRequestDto.setRequester(request.getUser().getId());
        participationRequestDto.setCreated(request.getCreated());
        return participationRequestDto;
    }

    public static EventRequestStatusUpdateResultDto toEventRequestStatusUpdateResultDtoFromRequestListEntity(List<Request> confirmedRequestList,
                                                                                                             List<Request> rejectedRequestList) {
        EventRequestStatusUpdateResultDto eventRequestStatusUpdateResultDto = new EventRequestStatusUpdateResultDto();
        eventRequestStatusUpdateResultDto.setConfirmedRequests(confirmedRequestList.stream()
                .map(RequestMapper::toParticipationRequestDtoFromRequestEntity)
                .collect(Collectors.toList()));
        eventRequestStatusUpdateResultDto.setRejectedRequests(rejectedRequestList.stream()
                .map(RequestMapper::toParticipationRequestDtoFromRequestEntity)
                .collect(Collectors.toList()));
        return eventRequestStatusUpdateResultDto;
    }
}
