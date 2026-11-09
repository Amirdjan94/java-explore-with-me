package ru.practicum.ewm.publicPart.events.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.privatePart.events.dto.EventFullDTO;
import ru.practicum.ewm.privatePart.events.service.EventService;
import ru.practicum.ewm.publicPart.events.data.SortParam;
import ru.practicum.ewm.utils.OffsetPageRequest;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@Validated
@Slf4j
public class PublicEventController {
    private EventService eventService;

    public PublicEventController(@Qualifier("eventServiceImpl") EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDTO getEventByEventIdForUnauthorizationUser(@PathVariable(name = "eventId") Long eventId,
                                                                HttpServletRequest request) {
        log.info("Поступил запрос на получение информации о событии с ID - {}, от неавторизованного пользователя ", eventId);
        return eventService.getEventByEventIdForUnauthorizationUser(eventId, request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDTO> getEventListForUnauthorizationUser(@RequestParam(name = "text", required = false) String text,
                                                                 @RequestParam(name = "categories", required = false) List<Long> categories,
                                                                 @RequestParam(name = "paid", required = false) Boolean paid,
                                                                 @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                                 @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                                 @RequestParam(name = "onlyAvailable", required = false, defaultValue = "false") Boolean onlyAvailable,
                                                                 @RequestParam(name = "sort", required = false) SortParam sort,
                                                                 @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
                                                                 @RequestParam(name = "size", defaultValue = "10", required = false) Integer size,
                                                                 HttpServletRequest request) {
        log.info("Поступил запрос на получение списка событий, c параметрами фильтров - " +
                "text = " + text + ";  paid = " + paid + "; categories = " + categories +
                "; rangeStart = " + rangeStart + "; rangeEnd = " + rangeEnd + "; from = " + from +
                "; size = " + size + "; onlyAvailable = " + onlyAvailable + "; sort = " + sort);
        final PageRequest pageRequest = OffsetPageRequest.of(from, size);
        return eventService.getEventListForUnauthorizationUser(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, pageRequest, request);
    }
}
