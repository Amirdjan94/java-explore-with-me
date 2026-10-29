package ru.practicum.ewm.privatePart.requests.service;

import ru.practicum.ewm.privatePart.requests.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto createParticipationRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getParticipationRequestDtoList(Long userId);

    ParticipationRequestDto canceledRequest(Long userId, Long requestId);
}
