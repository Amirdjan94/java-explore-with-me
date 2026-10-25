package ru.practicum.ewm.privatePart.events.service;

import ru.practicum.ewm.privatePart.events.dto.EventFullDTO;
import ru.practicum.ewm.privatePart.events.dto.NewEventDTO;

public interface EventService {
    EventFullDTO createEvent(NewEventDTO newEventDTO, Long userId);

    EventFullDTO getEventByUserIdAndEventId(Long userId, Long eventId);
}
