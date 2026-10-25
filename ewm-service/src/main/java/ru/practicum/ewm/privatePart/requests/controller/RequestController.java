package ru.practicum.ewm.privatePart.requests.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.privatePart.requests.dto.ParticipationRequestDto;
import ru.practicum.ewm.privatePart.requests.service.RequestService;

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

}
