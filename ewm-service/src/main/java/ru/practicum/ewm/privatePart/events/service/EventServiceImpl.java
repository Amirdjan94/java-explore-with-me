package ru.practicum.ewm.privatePart.events.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.admin.category.model.Category;
import ru.practicum.ewm.admin.category.repository.CategoryRepository;
import ru.practicum.ewm.admin.users.model.User;
import ru.practicum.ewm.admin.users.repository.UserRepository;
import ru.practicum.ewm.exception.ConditionsNotMetException;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.privatePart.events.dto.EventFullDTO;
import ru.practicum.ewm.privatePart.events.dto.NewEventDTO;
import ru.practicum.ewm.privatePart.events.mapper.EventMapper;
import ru.practicum.ewm.privatePart.events.model.Event;
import ru.practicum.ewm.privatePart.events.repository.EventRepository;

import java.util.Optional;

@Service
@Slf4j
public class EventServiceImpl implements EventService {

    private EventRepository eventRepository;
    private UserRepository userRepository;
    private CategoryRepository categoryRepository;

    public EventServiceImpl(@Qualifier("categoryRepository") CategoryRepository categoryRepository,
                            @Qualifier("eventRepository") EventRepository eventRepository,
                            @Qualifier("userRepository") UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
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
        return EventMapper.toEventFullDTOFromEventEntity(newEvent);
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
        return EventMapper.toEventFullDTOFromEventEntity(event);
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

}
