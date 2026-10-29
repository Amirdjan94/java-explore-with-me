package ru.practicum.ewm.privatePart.requests.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.privatePart.requests.dto.ParticipationRequestDto;
import ru.practicum.ewm.privatePart.requests.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users")
@Validated
@Slf4j
public class RequestController {
    private RequestService requestService;

    public RequestController(@Qualifier("requestServiceImpl") RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createParticipationRequest(@PathVariable(name = "userId") Long userId,
                                                              @RequestParam(name = "eventId") Long eventId) {
        log.info("Поступил запрос на участие в событии с ИД - {} , от пользователя с ИД - {}", eventId, userId);
        return requestService.createParticipationRequest(userId, eventId);
    }

    @GetMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getParticipationRequestDtoList(@PathVariable(name = "userId") Long userId) {
        log.info("Поступил запрос на получение информации о заявках пользователя на участие в чужих событиях, от пользователя с ИД - {}", userId);
        return requestService.getParticipationRequestDtoList(userId);
    }

    @PatchMapping("{userId}/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto canceledRequest(@PathVariable(name = "userId") Long userId,
                                                   @PathVariable(name = "requestId") Long requestId) {
        log.info("Поступил запрос на отмену своего запроса на участие в событии с ИД - {}, от пользователя с ИД - {}", requestId, userId);
        return requestService.canceledRequest(userId, requestId);
    }

}
