package ru.practicum.ewm.privatePart.events.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.privatePart.events.dto.EventFullDTO;
import ru.practicum.ewm.privatePart.events.dto.NewEventDTO;
import ru.practicum.ewm.privatePart.events.dto.UserUpdateEventRequestDto;
import ru.practicum.ewm.privatePart.events.service.EventService;
import ru.practicum.ewm.utils.OffsetPageRequest;

import java.util.List;

@RestController
@RequestMapping("/users")
@Validated
@Slf4j
public class EventController {
    private EventService eventService;

    public EventController(@Qualifier("eventServiceImpl") EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDTO createEvent(@Valid @RequestBody NewEventDTO newEventDTO,
                                    @PathVariable(name = "userId") Long userId) {
        log.info("Поступил запрос на добавление нового события - {} ", newEventDTO);
        return eventService.createEvent(newEventDTO, userId);
    }

    @GetMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDTO getEventByUserIdAndEventId(@PathVariable(name = "userId") Long userId,
                                                   @PathVariable(name = "eventId") Long eventId) {
        log.info("Поступил запрос на получение информации о событии с ID - {} , пользователя с ID - {}", eventId, userId);
        return eventService.getEventByUserIdAndEventId(userId, eventId);
    }

    @GetMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDTO> getEventListByUserId(@PathVariable(name = "userId") Long userId,
                                                   @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
                                                   @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        log.info("Поступил запрос, на получении списка событий, добавленных пользователем с ИД - {} ", userId);
        final PageRequest pageRequest = OffsetPageRequest.of(from, size);
        return eventService.getEventListByUserId(userId, pageRequest);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDTO updateEventByUserIdAndEventId(@PathVariable(name = "userId") Long userId,
                                                      @PathVariable(name = "eventId") Long eventId,
                                                      @RequestBody UserUpdateEventRequestDto userUpdateEventRequestDto) {
        log.info("Поступил запрос на изменения события с ID - {} , пользователя с ID - {}", eventId, userId);
        return eventService.updateEventByUserIdAndEventId(userId, eventId, userUpdateEventRequestDto);
    }


}
