package ru.practicum.ewm.privatePart.events.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.admin.events.dto.AdminUpdateEventRequestDto;
import ru.practicum.ewm.privatePart.events.data.State;
import ru.practicum.ewm.privatePart.events.dto.EventFullDTO;
import ru.practicum.ewm.privatePart.events.dto.NewEventDTO;
import ru.practicum.ewm.privatePart.events.dto.UserUpdateEventRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDTO createEvent(NewEventDTO newEventDTO, Long userId);

    EventFullDTO getEventByUserIdAndEventId(Long userId, Long eventId);

    List<EventFullDTO> getEventListByAdmin(List<Long> users, List<State> states, List<Long> categories,
                                           LocalDateTime rangeStart, LocalDateTime rangeEnd, PageRequest pageRequest);

    EventFullDTO updateEventByAdmin(AdminUpdateEventRequestDto adminUpdateEventRequestDto, Long eventId);

    List<EventFullDTO> getEventListByUserId(Long userId, PageRequest pageRequest);

    EventFullDTO updateEventByUserIdAndEventId(Long userId, Long eventId, UserUpdateEventRequestDto userUpdateEventRequestDto);
}
