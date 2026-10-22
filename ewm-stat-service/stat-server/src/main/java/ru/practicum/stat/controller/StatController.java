package ru.practicum.stat.controller;

import dto.HitDto;
import dto.StatsDto;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stat.service.StatService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping
@Validated
@Slf4j
public class StatController {

    StatService statService;

    public StatController(@Qualifier("statServiceImpl") StatService statService) {
        this.statService = statService;
    }

    @PostMapping(value = "hit", produces = "text/plain;charset=UTF-8")
    @ResponseStatus(HttpStatus.CREATED)
    public String createStat(@RequestBody @Valid HitDto hitDto) {
        log.info("Поступил запрос на создание записи в статистике - {}", hitDto);
        statService.createStat(hitDto);
        return "Информация сохранена";
    }

    @GetMapping("stats")
    public Collection<StatsDto> getAllStatsByParam(@RequestParam(value = "start")
                                                   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                   LocalDateTime start,
                                                   @RequestParam(value = "end")
                                                   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                   LocalDateTime end,
                                                   @RequestParam(value = "uris", required = false) List<String> uris,
                                                   @RequestParam(value = "unique", defaultValue = "false", required = false)
                                                   Boolean unique) {
        log.info("Поступил запрос на получение статистики: start - {}; " +
                "end - {}; uris - {}; uris - {};", start, end, uris, unique);
        return statService.getAllStatsByParam(start, end, uris, unique);
    }

}