package ru.practicum.ewm.privatePart.events.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.admin.events.dto.AdminUpdateEventRequestDto;
import ru.practicum.ewm.privatePart.events.data.State;
import ru.practicum.ewm.privatePart.events.dto.*;
import ru.practicum.ewm.privatePart.requests.dto.ParticipationRequestDto;
import ru.practicum.ewm.publicPart.events.data.SortParam;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDTO createEvent(NewEventDTO newEventDTO, Long userId);

    EventFullDTO getEventByUserIdAndEventId(Long userId, Long eventId);

    List<EventFullDTO> getEventListByAdmin(List<Long> users, List<State> states, List<Long> categories,
                                           LocalDateTime rangeStart, LocalDateTime rangeEnd, PageRequest pageRequest);

    EventFullDTO updateEventByAdmin(AdminUpdateEventRequestDto adminUpdateEventRequestDto, Long eventId);

    List<EventShortDTO> getEventListByUserId(Long userId, PageRequest pageRequest);

    EventFullDTO updateEventByUserIdAndEventId(Long userId, Long eventId, UserUpdateEventRequestDto userUpdateEventRequestDto);

    List<ParticipationRequestDto> getRequestByCurrentUserEvents(Long userId, Long eventId);

    EventRequestStatusUpdateResultDto updateRequestStatusByCurrentUserEvents(Long userId, Long eventId, EventRequestStatusUpdateRequestDto eventRequestStatusUpdateRequestDto);

    EventFullDTO getEventByEventIdForUnauthorizationUser(Long eventId, HttpServletRequest request);

    List<EventFullDTO> getEventListForUnauthorizationUser(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                                          LocalDateTime rangeEnd, Boolean onlyAvailable, SortParam sort,
                                                          PageRequest pageRequest, HttpServletRequest request);
}
