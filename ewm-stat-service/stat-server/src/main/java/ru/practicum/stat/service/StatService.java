package ru.practicum.stat.service;

import dto.HitDto;
import dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {

    void createStat(HitDto hitDto);

    List<StatsDto> getAllStatsByParam(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
