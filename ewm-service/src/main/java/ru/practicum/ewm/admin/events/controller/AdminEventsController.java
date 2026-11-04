package ru.practicum.ewm.admin.events.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.admin.events.dto.AdminUpdateEventRequestDto;
import ru.practicum.ewm.privatePart.events.data.State;
import ru.practicum.ewm.privatePart.events.dto.EventFullDTO;
import ru.practicum.ewm.privatePart.events.service.EventService;
import ru.practicum.ewm.utils.OffsetPageRequest;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@Validated
@Slf4j
public class AdminEventsController {
    private EventService eventService;

    public AdminEventsController(@Qualifier("eventServiceImpl") EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDTO> getEventListByAdmin(@RequestParam(name = "users", required = false) List<Long> users,
                                                  @RequestParam(name = "states", required = false) List<State> states,
                                                  @RequestParam(name = "categories", required = false) List<Long> categories,
                                                  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                  @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
                                                  @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        log.info("Поступил запрос на получение списка событий, c параметрами фильтров - " +
                "users = " + users + ";  states = " + states + "; categories = " + categories +
                "; rangeStart = " + rangeStart + "; rangeEnd = " + rangeEnd + "; from = " + from +
                "; size = " + size);
        final PageRequest pageRequest = OffsetPageRequest.of(from, size);
        return eventService.getEventListByAdmin(users, states, categories, rangeStart, rangeEnd, pageRequest);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDTO updateEventByAdmin(@Valid @RequestBody AdminUpdateEventRequestDto adminUpdateEventRequestDto,
                                           @PathVariable(name = "eventId") Long eventId) {
        log.info("Поступил запрос на обновление события с ИД - {} , на данные - {}", eventId, adminUpdateEventRequestDto);
        return eventService.updateEventByAdmin(adminUpdateEventRequestDto, eventId);
    }

}
