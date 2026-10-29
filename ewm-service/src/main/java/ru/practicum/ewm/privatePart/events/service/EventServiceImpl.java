package ru.practicum.ewm.privatePart.events.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.admin.category.model.Category;
import ru.practicum.ewm.admin.category.repository.CategoryRepository;
import ru.practicum.ewm.admin.events.data.AdminStateAction;
import ru.practicum.ewm.admin.events.dto.AdminUpdateEventRequestDto;
import ru.practicum.ewm.admin.users.model.User;
import ru.practicum.ewm.admin.users.repository.UserRepository;
import ru.practicum.ewm.exception.ConditionsNotMetException;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.privatePart.events.data.State;
import ru.practicum.ewm.privatePart.events.data.UserStateAction;
import ru.practicum.ewm.privatePart.events.dto.EventFullDTO;
import ru.practicum.ewm.privatePart.events.dto.NewEventDTO;
import ru.practicum.ewm.privatePart.events.dto.UserUpdateEventRequestDto;
import ru.practicum.ewm.privatePart.events.mapper.EventMapper;
import ru.practicum.ewm.privatePart.events.model.Event;
import ru.practicum.ewm.privatePart.events.repository.EventRepository;
import ru.practicum.ewm.privatePart.requests.data.RequestStatus;
import ru.practicum.ewm.privatePart.requests.model.Request;
import ru.practicum.ewm.privatePart.requests.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EventServiceImpl implements EventService {

    private EventRepository eventRepository;
    private UserRepository userRepository;
    private CategoryRepository categoryRepository;
    private RequestRepository requestRepository;

    public EventServiceImpl(@Qualifier("categoryRepository") CategoryRepository categoryRepository,
                            @Qualifier("eventRepository") EventRepository eventRepository,
                            @Qualifier("userRepository") UserRepository userRepository,
                            @Qualifier("requestRepository") RequestRepository requestRepository) {
        this.categoryRepository = categoryRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    public EventFullDTO createEvent(NewEventDTO newEventDTO, Long userId) {
        log.info("Проверяем наличие пользователя в БД с ID - {} ", userId);
        User user = checkUserIdAndGetUserEntity(userId);
        log.info("Проверяем наличие категории в БД с ID - {} ", newEventDTO.getCategory());
        Category category = checkCategoryIdAndGetCategoryEntity(newEventDTO.getCategory());
        Event event = EventMapper.toEventFronNewEventDto(newEventDTO, category, user);
        Event newEvent = eventRepository.save(event);
        log.info("Создали новое событие и сохранили в БД - {} ", newEvent);
        return EventMapper.toEventFullDTOFromEventEntity(newEvent, 0L);
    }

    @Override
    public EventFullDTO getEventByUserIdAndEventId(Long userId, Long eventId) {
        log.info("Проверяем наличие пользователя в БД с ID - {} ", userId);
        checkUserIdAndGetUserEntity(userId);
        log.info("Проверяем наличие события в БД с ID - {} ", eventId);
        Event event = checkEventIdAndGetEventEntity(eventId);
        if (event.getUser().getId() != userId) {
            log.warn("События с указанным ID={} и с указанным пользователем userId={} не существует ", eventId, userId);
            throw new ObjectNotFoundException("Event with id=" + eventId + " was not found");
        }
        log.info("Запрашиваем в БД информацию о количестве одобренных заявок на участие в данном событии");
        Long confirmedRequestCount = requestRepository.getRequestCountByEventIdAndStatus(eventId,
                RequestStatus.CONFIRMED);
        return EventMapper.toEventFullDTOFromEventEntity(event, confirmedRequestCount);
    }

    @Override
    public EventFullDTO updateEventByUserIdAndEventId(Long userId, Long eventId, UserUpdateEventRequestDto userUpdateEventRequestDto) {
        log.info("Проверяем наличие пользователя в БД с ID - {} ", userId);
        checkUserIdAndGetUserEntity(userId);
        log.info("Проверяем наличие события в БД с ID - {} ", eventId);
        Event event = checkEventIdAndGetEventEntity(eventId);
        if (event.getUser().getId() != userId) {
            log.warn("События с указанным ID={} и с указанным пользователем userId={} не существует ", eventId, userId);
            throw new ObjectNotFoundException("Event with id=" + eventId + " was not found");
        }
        log.info("Проверяем запрос на бизнес условия");
        checkEventUpdateByUserRule(userUpdateEventRequestDto, event);
        log.info("Обновляем событие");
        updateEventByUser(userUpdateEventRequestDto, event);
        Event newEvent = eventRepository.save(event);
        Long confirmedRequestCount = requestRepository.getRequestCountByEventIdAndStatus(eventId,
                RequestStatus.CONFIRMED);
        return EventMapper.toEventFullDTOFromEventEntity(newEvent, confirmedRequestCount);
    }

    @Override
    public List<EventFullDTO> getEventListByAdmin(List<Long> users, List<State> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, PageRequest pageRequest) {
        log.info("Запрашиваем в БД список событий по указанным фильтрам");
        Page<Event> eventPage = eventRepository.findByParams(users, states, categories, rangeStart, rangeEnd, pageRequest);
        List<Long> eventIds = eventPage.get()
                .map((el) -> el.getId())
                .collect(Collectors.toList());  // Получили список ID событий
        Map<Long, Long> requestMap = getRequestMapByEventIds(eventIds); // Собрали мапу: ключ - id события; значение - количество запросов
        return eventPage.get()
                .map((el) -> EventMapper.toEventFullDTOFromEventEntity(
                        el,
                        requestMap.get(el.getId()) == null ? 0 : requestMap.get(el.getId()))
                )
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDTO updateEventByAdmin(AdminUpdateEventRequestDto adminUpdateEventRequestDto, Long eventId) {
        log.info("Проверяем наличие события в БД с ID - {} ", eventId);
        Event event = checkEventIdAndGetEventEntity(eventId);
        log.info("Проверяем запрос на бизнес условия");
        checkEventUpdateByAdminRule(adminUpdateEventRequestDto, event);
        log.info("Обновляем событие");
        updateEventByAdmin(adminUpdateEventRequestDto, event);
        log.info("Сохраняем в БД измененное событие - {}", event);
        Event newEvent = eventRepository.save(event);
        Long confirmedRequestCount = requestRepository.getRequestCountByEventIdAndStatus(eventId,
                RequestStatus.CONFIRMED);
        return EventMapper.toEventFullDTOFromEventEntity(newEvent, confirmedRequestCount);
    }

    @Override
    public List<EventFullDTO> getEventListByUserId(Long userId, PageRequest pageRequest) {
        log.info("Проверяем наличие пользователя в БД с ID - {} ", userId);
        checkUserIdAndGetUserEntity(userId);
        log.info("Запрашиваем в БД список событий, добавленных пользователем с ИД - {}", userId);
        Page<Event> eventPage = eventRepository.findByUserId(userId, pageRequest);
        List<Long> eventIds = eventPage.get()
                .map((el) -> el.getId())
                .collect(Collectors.toList());  // Получили список ID событий
        Map<Long, Long> requestMap = getRequestMapByEventIds(eventIds); // Собрали мапу: ключ - id события; значение - количество запросов
        return eventPage.get()
                .map((el) -> EventMapper.toEventFullDTOFromEventEntity(
                        el,
                        requestMap.get(el.getId()) == null ? 0 : requestMap.get(el.getId()))
                )
                .collect(Collectors.toList());
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

    private Category checkCategoryIdAndGetCategoryEntity(Long categoryId) {
        if (categoryId == null || categoryId <= 0) {
            log.warn("Не корректное Id пользователя - " + categoryId);
            throw new ConditionsNotMetException("Incorrect category Id");
        }
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()) {
            log.warn("Категории с указанным ID - {} не существует ", categoryId);
            throw new ObjectNotFoundException("Category with id=" + categoryId + " was not found");
        }
        return category.get();
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

    private void checkEventUpdateByAdminRule(AdminUpdateEventRequestDto adminUpdateEventRequestDto, Event event) {
        if (adminUpdateEventRequestDto.getStateAction().equals(AdminStateAction.REJECT_EVENT) &&
                (event.getState().equals(State.PUBLISHED) || event.getState().equals(State.CANCELED))) {
            log.warn("Событие можно отклонить, только если оно еще не опубликовано или уже отменено");
            throw new DataIntegrityViolationException("An event can be rejected only if it has not yet been published or canceled.");
        }
        if (adminUpdateEventRequestDto.getStateAction().equals(AdminStateAction.PUBLISH_EVENT) &&
                !event.getState().equals(State.PENDING)) {
            log.warn("Событие можно публиковать, только если оно в состоянии ожидания публикации");
            throw new DataIntegrityViolationException("An event can be published only if it is in a " +
                    "pending-publication state.");
        }
        if (adminUpdateEventRequestDto.getStateAction().equals(AdminStateAction.PUBLISH_EVENT) &&
                LocalDateTime.now().isAfter(event.getEventDate().plusHours(1))) {
            log.warn("Дата начала изменяемого события должна быть не ранее чем за час от даты публикации");
            throw new DataIntegrityViolationException("The start date of the event being modified must be no earlier " +
                    "than one hour from the publication date.");
        }
    }

    private void checkEventUpdateByUserRule(UserUpdateEventRequestDto userUpdateEventRequestDto, Event event) {
        if (!event.getState().equals(State.CANCELED) && !event.getState().equals(State.PENDING)) {
            log.warn("Изменить можно только отмененные события или события в состоянии ожидания модерации");
            throw new ForbiddenException("Only pending or canceled events can be changed");
        }
        if (userUpdateEventRequestDto.getEventDate() != null &&
                LocalDateTime.now().isAfter(userUpdateEventRequestDto.getEventDate().plusHours(2))) {
            log.warn("Дата и время на которые намечено событие не может быть раньше, чем через два часа " +
                    "от текущего момента");
            throw new DataIntegrityViolationException("The date and time scheduled for the event cannot be less than " +
                    "two hours from the current moment.");
        }
    }

    private void updateEventByAdmin(AdminUpdateEventRequestDto adminUpdateEventRequestDto, Event event) {
        if (adminUpdateEventRequestDto.getDescription() != null &&
                !adminUpdateEventRequestDto.getDescription().isBlank()) {
            event.setDescription(adminUpdateEventRequestDto.getDescription());
        }
        if (adminUpdateEventRequestDto.getAnnotation() != null &&
                !adminUpdateEventRequestDto.getAnnotation().isBlank()) {
            event.setAnnotation(adminUpdateEventRequestDto.getAnnotation());
        }
        if (adminUpdateEventRequestDto.getCategory() != null &&
                !adminUpdateEventRequestDto.getCategory().equals(event.getCategory().getId())) {
            event.setCategory(categoryRepository.findById(adminUpdateEventRequestDto.getCategory()).get());
        }
        if (adminUpdateEventRequestDto.getLocation() != null &&
                !adminUpdateEventRequestDto.getLocation().equals(event.getLocation())) {
            event.setLocation(adminUpdateEventRequestDto.getLocation());
        }
        if (adminUpdateEventRequestDto.getEventDate() != null &&
                !adminUpdateEventRequestDto.getEventDate().equals(event.getEventDate())) {
            event.setEventDate(adminUpdateEventRequestDto.getEventDate());
        }
        if (adminUpdateEventRequestDto.getPaid() != null &&
                !adminUpdateEventRequestDto.getPaid().equals(event.getPaid())) {
            event.setPaid(adminUpdateEventRequestDto.getPaid());
        }
        if (adminUpdateEventRequestDto.getParticipantLimit() != null &&
                !adminUpdateEventRequestDto.getParticipantLimit().equals(event.getParticipantLimit())) {
            event.setParticipantLimit(adminUpdateEventRequestDto.getParticipantLimit());
        }
        if (adminUpdateEventRequestDto.getRequestModeration() != null &&
                !adminUpdateEventRequestDto.getRequestModeration().equals(event.getRequestModeration())) {
            event.setRequestModeration(adminUpdateEventRequestDto.getRequestModeration());
        }
        if (adminUpdateEventRequestDto.getTitle() != null &&
                !adminUpdateEventRequestDto.getTitle().equals(event.getTitle())) {
            event.setTitle(adminUpdateEventRequestDto.getTitle());
        }
        if (adminUpdateEventRequestDto.getStateAction() != null) {
            if (adminUpdateEventRequestDto.getStateAction().equals(AdminStateAction.PUBLISH_EVENT)) {
                event.setState(State.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            }
            if (adminUpdateEventRequestDto.getStateAction().equals(AdminStateAction.REJECT_EVENT)) {
                event.setState(State.CANCELED);
            }
        }
    }

    private void updateEventByUser(UserUpdateEventRequestDto userUpdateEventRequestDto, Event event) {
        if (userUpdateEventRequestDto.getDescription() != null &&
                !userUpdateEventRequestDto.getDescription().isBlank()) {
            event.setDescription(userUpdateEventRequestDto.getDescription());
        }
        if (userUpdateEventRequestDto.getAnnotation() != null &&
                !userUpdateEventRequestDto.getAnnotation().isBlank()) {
            event.setAnnotation(userUpdateEventRequestDto.getAnnotation());
        }
        if (userUpdateEventRequestDto.getCategory() != null &&
                !userUpdateEventRequestDto.getCategory().equals(event.getCategory().getId())) {
            event.setCategory(categoryRepository.findById(userUpdateEventRequestDto.getCategory()).get());
        }
        if (userUpdateEventRequestDto.getLocation() != null &&
                !userUpdateEventRequestDto.getLocation().equals(event.getLocation())) {
            event.setLocation(userUpdateEventRequestDto.getLocation());
        }
        if (userUpdateEventRequestDto.getEventDate() != null &&
                !userUpdateEventRequestDto.getEventDate().equals(event.getEventDate())) {
            event.setEventDate(userUpdateEventRequestDto.getEventDate());
        }
        if (userUpdateEventRequestDto.getPaid() != null &&
                !userUpdateEventRequestDto.getPaid().equals(event.getPaid())) {
            event.setPaid(userUpdateEventRequestDto.getPaid());
        }
        if (userUpdateEventRequestDto.getParticipantLimit() != null &&
                !userUpdateEventRequestDto.getParticipantLimit().equals(event.getParticipantLimit())) {
            event.setParticipantLimit(userUpdateEventRequestDto.getParticipantLimit());
        }
        if (userUpdateEventRequestDto.getRequestModeration() != null &&
                !userUpdateEventRequestDto.getRequestModeration().equals(event.getRequestModeration())) {
            event.setRequestModeration(userUpdateEventRequestDto.getRequestModeration());
        }
        if (userUpdateEventRequestDto.getTitle() != null &&
                !userUpdateEventRequestDto.getTitle().equals(event.getTitle())) {
            event.setTitle(userUpdateEventRequestDto.getTitle());
        }
        if (userUpdateEventRequestDto.getStateAction() != null) {
            if (userUpdateEventRequestDto.getStateAction().equals(UserStateAction.CANCEL_REVIEW)) {
                event.setState(State.CANCELED);
                event.setPublishedOn(LocalDateTime.now());
            }
//            if (userUpdateEventRequestDto.getUserStateAction().equals(UserStateAction.SEND_TO_REVIEW)) {
//                event.setState(State.PENDING);
//            }
        }
    }

    private Map<Long, Long> getRequestMapByEventIds(List<Long> eventIds) {
        List<Request> requestList = requestRepository.findAllRequestByEventIds(eventIds, RequestStatus.CONFIRMED);
        Map<Long, Long> requestMap = new HashMap<>();
        for (Request request : requestList) {
            Long key = request.getEvent().getId();
            if (requestMap.containsKey(key)) {
                requestMap.put(
                        key,
                        requestMap.get(request.getEvent().getId()) + 1
                );
            } else {
                requestMap.put(
                        key,
                        1L
                );
            }
        }
        return requestMap;
    }

}
