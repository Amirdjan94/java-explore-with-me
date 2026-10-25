package ru.practicum.ewm.privatePart.requests.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.admin.users.model.User;
import ru.practicum.ewm.admin.users.repository.UserRepository;
import ru.practicum.ewm.exception.ConditionsNotMetException;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.privatePart.events.data.State;
import ru.practicum.ewm.privatePart.events.model.Event;
import ru.practicum.ewm.privatePart.events.repository.EventRepository;
import ru.practicum.ewm.privatePart.requests.dto.ParticipationRequestDto;
import ru.practicum.ewm.privatePart.requests.mapper.RequestMapper;
import ru.practicum.ewm.privatePart.requests.model.Request;
import ru.practicum.ewm.privatePart.requests.repository.RequestRepository;

import java.util.Optional;

@Service
@Slf4j
public class RequestServiceImpl implements RequestService {

    private EventRepository eventRepository;
    private UserRepository userRepository;
    private RequestRepository requestRepository;

    public RequestServiceImpl(@Qualifier("eventRepository") EventRepository eventRepository,
                              @Qualifier("userRepository") UserRepository userRepository,
                              @Qualifier("requestRepository") RequestRepository requestRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    public ParticipationRequestDto createParticipationRequest(Long userId, Long eventId) {
        log.info("Проверяем наличие пользователя в БД с ID - {} ", userId);
        User user = checkUserIdAndGetUserEntity(userId);
        log.info("Проверяем наличие события в БД с ID - {} ", eventId);
        Event event = checkEventIdAndGetEventEntity(eventId);
        log.info("Проверяем запрос на бизнес условия");
        checkRequestRule(user, event);
        Request request = RequestMapper.toRequestFromUserAndEvent(user, event);
        Request newRequest = requestRepository.save(request);
        log.info("Создали новый запрос и сохранили в БД - {} ", newRequest);
        return RequestMapper.toParticipationRequestDtoFromRequestEntity(newRequest);
    }

    private Event checkEventIdAndGetEventEntity(Long eventId) {
        if (eventId == null || eventId <= 0) {
            log.warn("Не корректное Id события - " + eventId);
            throw new ConditionsNotMetException("Incorrect event Id");
        }
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            log.warn("События с указанным ID - {} не существует ", eventId);
            throw new ObjectNotFoundException("Event with id=" + eventId + " was not found");
        }
        return event.get();
    }

    private User checkUserIdAndGetUserEntity(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ConditionsNotMetException("Incorrect userId");
        }
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            log.warn("Пользователя с указанным ID - {} не существует ", userId);
            throw new ObjectNotFoundException("User with id=" + userId + " was not found");
        }
        return user.get();
    }

    private void checkRequestRule(User user, Event event) {
        if (event.getUser().getId().equals(user.getId())) {
            log.warn("Инициатор события не может добавить запрос на участие в своём событии");
            throw new DataIntegrityViolationException("The event is related to the current user.");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            log.warn("Нельзя участвовать в неопубликованном событии");
            throw new DataIntegrityViolationException("Cannot participate in an unpublished event.");
        }
        if (event.getParticipantLimit() != 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            log.warn("Достигнут лимит запросов на участие");
            throw new DataIntegrityViolationException("The limit for participation requests has been reached.");
        }
    }

}
