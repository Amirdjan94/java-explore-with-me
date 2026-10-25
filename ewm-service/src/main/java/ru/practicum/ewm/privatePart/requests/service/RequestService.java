package ru.practicum.ewm.privatePart.requests.service;

import ru.practicum.ewm.privatePart.requests.dto.ParticipationRequestDto;

public interface RequestService {
    ParticipationRequestDto createParticipationRequest(Long userId, Long eventId);
}
