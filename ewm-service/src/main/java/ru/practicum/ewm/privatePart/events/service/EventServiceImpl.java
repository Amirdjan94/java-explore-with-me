package ru.practicum.ewm.privatePart.events.service;

import client.StatClient;
import dto.HitDto;
import dto.StatsDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.admin.category.model.Category;
import ru.practicum.ewm.admin.category.repository.CategoryRepository;
import ru.practicum.ewm.admin.events.data.AdminStateAction;
import ru.practicum.ewm.admin.events.dto.AdminUpdateEventRequestDto;
import ru.practicum.ewm.admin.users.model.User;
import ru.practicum.ewm.admin.users.repository.UserRepository;
import ru.practicum.ewm.exception.ConditionsNotMetException;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.privatePart.events.data.State;
import ru.practicum.ewm.privatePart.events.data.UserStateAction;
import ru.practicum.ewm.privatePart.events.dto.*;
import ru.practicum.ewm.privatePart.events.mapper.EventMapper;
import ru.practicum.ewm.privatePart.events.model.Event;
import ru.practicum.ewm.privatePart.events.repository.EventRepository;
import ru.practicum.ewm.privatePart.requests.data.RequestStatus;
import ru.practicum.ewm.privatePart.requests.dto.ParticipationRequestDto;
import ru.practicum.ewm.privatePart.requests.mapper.RequestMapper;
import ru.practicum.ewm.privatePart.requests.model.Request;
import ru.practicum.ewm.privatePart.requests.repository.RequestRepository;
import ru.practicum.ewm.publicPart.events.data.SortParam;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private EventRepository eventRepository;
    private UserRepository userRepository;
    private CategoryRepository categoryRepository;
    private RequestRepository requestRepository;
    private StatClient statClient;

    public EventServiceImpl(@Qualifier("categoryRepository") CategoryRepository categoryRepository,
                            @Qualifier("eventRepository") EventRepository eventRepository,
                            @Qualifier("userRepository") UserRepository userRepository,
                            @Qualifier("requestRepository") RequestRepository requestRepository,
                            StatClient statClient) {
        this.categoryRepository = categoryRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.statClient = statClient;
    }

    @Override
    @Transactional
    public EventFullDTO createEvent(NewEventDTO newEventDTO, Long userId) {
        log.info("Проверяем наличие пользователя в БД с ID - {} ", userId);
        User user = checkUserIdAndGetUserEntity(userId);
        log.info("Проверяем наличие категории в БД с ID - {} ", newEventDTO.getCategory());
        Category category = checkCategoryIdAndGetCategoryEntity(newEventDTO.getCategory());
        Event event = EventMapper.toEventFronNewEventDto(newEventDTO, category, user);
        Event newEvent = eventRepository.save(event);
        log.info("Создали новое событие и сохранили в БД - {} ", newEvent);
        return EventMapper.toEventFullDTOFromEventEntity(newEvent, 0L, 0L);
    }

    @Override
    public EventFullDTO getEventByUserIdAndEventId(Long userId, Long eventId) {
        log.info("Проверяем наличие пользователя в БД с ID - {} ", userId);
        checkUserIdAndGetUserEntity(userId);
        log.info("Проверяем наличие события в БД с ID - {} ", eventId);
        Event event = checkEventIdAndGetEventEntity(eventId);
        if (!event.getUser().getId().equals(userId)) {
            log.warn("События с указанным ID={} и с указанным пользователем userId={} не существует ", eventId, userId);
            throw new ObjectNotFoundException("Event with id=" + eventId + " was not found");
        }
        log.info("Запрашиваем в БД информацию о количестве одобренных заявок на участие в данном событии");
        Long confirmedRequestCount = requestRepository.getRequestCountByEventIdAndStatus(eventId,
                RequestStatus.CONFIRMED);
        Map<Long, Long> viewMap = getViewMapByEventIds(List.of(eventId)); // Собрали мапу: ключ - id события; значение - количество просмотров
        return EventMapper.toEventFullDTOFromEventEntity(event, confirmedRequestCount,
                viewMap.get(eventId) == null ? 0 : viewMap.get(eventId));
    }

    @Override
    @Transactional
    public EventFullDTO updateEventByUserIdAndEventId(Long userId, Long eventId, UserUpdateEventRequestDto userUpdateEventRequestDto) {
        log.info("Проверяем наличие пользователя в БД с ID - {} ", userId);
        checkUserIdAndGetUserEntity(userId);
        log.info("Проверяем наличие события в БД с ID - {} ", eventId);
        Event event = checkEventIdAndGetEventEntity(eventId);
        if (!event.getUser().getId().equals(userId)) {
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
        Map<Long, Long> viewMap = getViewMapByEventIds(List.of(eventId)); // Собрали мапу: ключ - id события; значение - количество просмотров
        return EventMapper.toEventFullDTOFromEventEntity(newEvent, confirmedRequestCount,
                viewMap.get(eventId) == null ? 0 : viewMap.get(eventId));
    }

    @Override
    public List<EventFullDTO> getEventListByAdmin(List<Long> users, List<State> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, PageRequest pageRequest) {
        log.info("Запрашиваем в БД список событий по указанным фильтрам");
        Page<Event> eventPage = eventRepository.findByParams(users, states, categories, rangeStart, rangeEnd, pageRequest);
        List<Long> eventIds = eventPage.get()
                .map((el) -> el.getId())
                .collect(Collectors.toList());  // Получили список ID событий
        System.out.println("Page<Event> eventPage = " + eventPage.get().toList());
        Map<Long, Long> requestMap = getRequestMapByEventIds(eventIds); // Собрали мапу: ключ - id события; значение - количество запросов
        Map<Long, Long> viewMap = getViewMapByEventIds(eventIds); // Собрали мапу: ключ - id события; значение - количество просмотров
        return eventPage.get()
                .map((el) -> EventMapper.toEventFullDTOFromEventEntity(
                        el,
                        requestMap.get(el.getId()) == null ? 0 : requestMap.get(el.getId()),
                        viewMap.get(el.getId()) == null ? 0 : viewMap.get(el.getId()))
                )
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
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
        Map<Long, Long> viewMap = getViewMapByEventIds(List.of(eventId)); // Собрали мапу: ключ - id события; значение - количество просмотров
        return EventMapper.toEventFullDTOFromEventEntity(newEvent, confirmedRequestCount,
                viewMap.get(eventId) == null ? 0 : viewMap.get(eventId));
    }

    @Override
    public List<EventShortDTO> getEventListByUserId(Long userId, PageRequest pageRequest) {
        log.info("Проверяем наличие пользователя в БД с ID - {} ", userId);
        checkUserIdAndGetUserEntity(userId);
        log.info("Запрашиваем в БД список событий, добавленных пользователем с ИД - {}", userId);
        Page<Event> eventPage = eventRepository.findByUserId(userId, pageRequest);
        List<Long> eventIds = eventPage.get()
                .map((el) -> el.getId())
                .collect(Collectors.toList());  // Получили список ID событий
        Map<Long, Long> requestMap = getRequestMapByEventIds(eventIds); // Собрали мапу: ключ - id события; значение - количество запросов
        Map<Long, Long> viewMap = getViewMapByEventIds(eventIds); // Собрали мапу: ключ - id события; значение - количество просмотров
        return eventPage.get()
                .map((el) -> EventMapper.toEventShortDTOFromEventEntity(
                        el,
                        requestMap.get(el.getId()) == null ? 0 : requestMap.get(el.getId()),
                        viewMap.get(el.getId()) == null ? 0 : viewMap.get(el.getId()))
                )
                .collect(Collectors.toList());
    }

    @Override
    public List<ParticipationRequestDto> getRequestByCurrentUserEvents(Long userId, Long eventId) {
        log.info("Проверяем наличие пользователя в БД с ID - {} ", userId);
        checkUserIdAndGetUserEntity(userId);
        log.info("Проверяем наличие события в БД с ID - {} ", eventId);
        Event event = checkEventIdAndGetEventEntity(eventId);
        log.info("Проверяем относится ли событие к текущему пользователю");
        if (!event.getUser().getId().equals(userId)) {
            log.warn("Пользователя с указанным ID - {} не существует ", userId);
            throw new ConditionsNotMetException("Event with id=" + eventId + " was not refer user with id=" + userId);
        }
        List<Request> requestList = requestRepository.findAllRequestByEventId(eventId);

        return requestList.stream()
                .map(RequestMapper::toParticipationRequestDtoFromRequestEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResultDto updateRequestStatusByCurrentUserEvents(Long userId, Long eventId, EventRequestStatusUpdateRequestDto eventRequestStatusUpdateRequestDto) {
        log.info("Проверяем наличие пользователя в БД с ID - {} ", userId);
        checkUserIdAndGetUserEntity(userId);
        log.info("Проверяем наличие события в БД с ID - {} ", eventId);
        Event event = checkEventIdAndGetEventEntity(eventId);
        log.info("Проверяем относится ли событие к текущему пользователю");
        if (!event.getUser().getId().equals(userId)) {
            log.warn("Пользователя с указанным ID - {} не существует ", userId);
            throw new ConditionsNotMetException("Event with id=" + eventId + " was not refer user with id=" + userId);
        }
        List<Request> requestList = requestRepository.findAllRequestByEventId(eventId);
        log.info("Запускаем проверку бизнес условий");
        checkUpdateRequestStatusByCurrentUserEventsRule(event, requestList, eventRequestStatusUpdateRequestDto);
        log.info("Сохраняем изменения в БД");
        requestRepository.updateUsersStatus(eventRequestStatusUpdateRequestDto.getStatus(),
                new ArrayList<>(eventRequestStatusUpdateRequestDto.getRequestIds()));
        List<Request> confirmedRequestList = requestRepository.findAllRequestByEventIds(List.of(eventId),
                RequestStatus.CONFIRMED);
        List<Request> rejectedRequestList = requestRepository.findAllRequestByEventIds(List.of(eventId),
                RequestStatus.REJECTED);
        return RequestMapper.toEventRequestStatusUpdateResultDtoFromRequestListEntity(confirmedRequestList, rejectedRequestList);
    }

    @Override
    public EventFullDTO getEventByEventIdForUnauthorizationUser(Long eventId, HttpServletRequest request) {
        log.info("Проверяем наличие события в БД с ID - {} ", eventId);
        Event event = checkEventIdAndGetEventEntity(eventId);
        log.info("Проверяем статус события");
        if (!event.getState().equals(State.PUBLISHED)) {
            log.warn("Не опубликованное событие нельзя отображать не авторизованным пользователям");
            throw new ObjectNotFoundException("Event with id=" + eventId + " not published");
        }
        log.info("Отправляем информацию в сервис статистики");
        statClient.createStat(new HitDto("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now()));
        log.info("Получаем данные из БД и отправляем пользователю");
        Long confirmedRequestCount = requestRepository.getRequestCountByEventIdAndStatus(eventId,
                RequestStatus.CONFIRMED);
        Map<Long, Long> viewMap = getViewMapByEventIds(List.of(eventId)); // Собрали мапу: ключ - id события; значение - количество просмотров
        return EventMapper.toEventFullDTOFromEventEntity(event, confirmedRequestCount,
                viewMap.get(eventId) == null ? 0 : viewMap.get(eventId));
    }

    @Override
    public List<EventFullDTO> getEventListForUnauthorizationUser(String text, List<Long> categories, Boolean paid,
                                                                 LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                                 Boolean onlyAvailable, SortParam sort,
                                                                 PageRequest pageRequest, HttpServletRequest request) {
        log.info("Отправляем информацию в сервис статистики");
        statClient.createStat(new HitDto("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now()));
        if (rangeEnd != null && rangeEnd.isBefore(LocalDateTime.now())) {
            log.warn("Событие должно быть опубликовано");
            throw new ConditionsNotMetException("Event must be published");
        }
        log.info("Запрашиваем в БД список событий по указанным фильтрам");
        Page<Event> eventPage = eventRepository
                .findByParamsForUnauthorizationUser(text, categories, paid, rangeStart, rangeEnd, pageRequest);
        List<Long> eventIds = eventPage.get()
                .map((el) -> el.getId())
                .collect(Collectors.toList());  // Получили список ID событий
        Map<Long, Long> requestMap = getRequestMapByEventIds(eventIds); // Собрали мапу: ключ - id события; значение - количество запросов
        Map<Long, Long> viewMap = getViewMapByEventIds(eventIds); // Собрали мапу: ключ - id события; значение - количество просмотров
        List<EventFullDTO> eventFullDTOList = eventPage.get()
                .map((el) -> EventMapper.toEventFullDTOFromEventEntity(
                        el,
                        requestMap.get(el.getId()) == null ? 0 : requestMap.get(el.getId()),
                        viewMap.get(el.getId()) == null ? 0 : viewMap.get(el.getId()))
                )
                .collect(Collectors.toList());
        if (onlyAvailable) {
            eventFullDTOList = eventFullDTOList.stream()
                    .filter(el ->
                            el.getConfirmedRequests() < el.getParticipantLimit() ||
                                    el.getParticipantLimit() == 0
                    )
                    .collect(Collectors.toList());
        }
        if (SortParam.VIEWS.equals(sort)) {
            eventFullDTOList = eventFullDTOList.stream()
                    .sorted(Comparator.comparing(EventFullDTO::getViews))
                    .collect(Collectors.toList());
        }

        return eventFullDTOList;
    }

    private Map<Long, Long> getViewMapByEventIds(List<Long> eventIds) {
        List<String> urisList = eventIds.stream()
                .map(el -> "/events/" + el)
                .collect(Collectors.toList());
        Map<String, Object> statPayload = new HashMap<>();
        statPayload.put("uris", urisList);
        statPayload.put("unique", "true");
        statPayload.put("start", LocalDateTime.now().minusYears(30));
        statPayload.put("end", LocalDateTime.now().plusYears(30));
        ResponseEntity<List<StatsDto>> statList = statClient.getAllStats(statPayload);
        Map<Long, Long> viewMap = new HashMap<>();
        for (Long eventId : eventIds) {
            Long hitCount = 0L;
            if (!statList.getBody().isEmpty()) {
                for (StatsDto statsDto : statList.getBody()) {
                    if (statsDto.getUri().equals("/events/" + eventId)) {
                        hitCount = statsDto.getHits();
                    }
                }
            }
            viewMap.put(
                    eventId,
                    hitCount != null ? hitCount : 0
            );
        }
        return viewMap;
    }

    private void checkUpdateRequestStatusByCurrentUserEventsRule(Event event, List<Request> currentRequestList,
                                                                 EventRequestStatusUpdateRequestDto eventRequestStatusUpdateRequestDto) {
        for (Long requestId : eventRequestStatusUpdateRequestDto.getRequestIds()) {
            // Проверяем что все requestId существуют и относятся к текущему событию
            if (currentRequestList.stream().filter(
                    el -> el.getId().equals(requestId)
            ).count() == 0) {
                log.warn("Запроса с указанным ID - {} не существует ", requestId);
                throw new ObjectNotFoundException("Event with id=" + requestId + " was not found");
            }
        }

        // Получаем список запросов по переданным ID (без похода в БД)
        List<Request> requestList = new ArrayList<>(currentRequestList);
        for (Request request : currentRequestList) {
            if (!eventRequestStatusUpdateRequestDto.getRequestIds().contains(request.getId())) {
                requestList.remove(request);
            }
        }

        if (requestList.stream().filter(
                el -> el.getRequestStatus().equals(RequestStatus.PENDING)
        ).count() != eventRequestStatusUpdateRequestDto.getRequestIds().size()) {
            // Статус можно изменить только у заявок, находящихся в состоянии ожидания
            log.warn("Не все переданные заявки находятся в статусе ожидания ");
            throw new DataIntegrityViolationException("Request must have status PENDING");
        }

        if (!(event.getParticipantLimit() == 0 || !event.getRequestModeration()) &&
                eventRequestStatusUpdateRequestDto.getStatus().equals(RequestStatus.CONFIRMED)) {
            // все заявки подтверждаем если лимит равен нулю или отключена премодерация, иначе проверяем лимит
            long confirmedRequestCount = currentRequestList.stream().filter(el ->
                    el.getRequestStatus().equals(RequestStatus.CONFIRMED)).count();
            if (event.getParticipantLimit() < confirmedRequestCount + requestList.size()) {
                // Если достигнут лимит заявок, то отображаем ошибку
                log.warn("Превышен лимит заявок");
                throw new DataIntegrityViolationException("The participant limit has been reached");
            }
        }

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
        if (adminUpdateEventRequestDto.getStateAction() != null) {
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
                    LocalDateTime.now().plusHours(1).isAfter(event.getEventDate())) {
                log.warn("Дата начала изменяемого события должна быть не ранее чем за час от даты публикации");
                throw new DataIntegrityViolationException("The start date of the event being modified must be no earlier " +
                        "than one hour from the publication date.");
            }
        }

    }

    private void checkEventUpdateByUserRule(UserUpdateEventRequestDto userUpdateEventRequestDto, Event event) {
        if (!event.getState().equals(State.CANCELED) && !event.getState().equals(State.PENDING)) {
            log.warn("Изменить можно только отмененные события или события в состоянии ожидания модерации");
            throw new DataIntegrityViolationException("Only pending or canceled events can be changed");
        }
        if (userUpdateEventRequestDto.getEventDate() != null &&
                LocalDateTime.now().plusHours(2).isAfter(userUpdateEventRequestDto.getEventDate())) {
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
            if (userUpdateEventRequestDto.getStateAction().equals(UserStateAction.SEND_TO_REVIEW)) {
                event.setState(State.PENDING);
            }
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
