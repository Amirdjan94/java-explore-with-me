package ru.practicum.stat.mapper;

import dto.HitDto;
import dto.StatsDto;
import ru.practicum.stat.model.Stat;

public class StatMapper {
    public static Stat toStat(HitDto hitDto) {
        Stat stat = new Stat();
        stat.setIp(hitDto.getIp());
        stat.setUri(hitDto.getUri());
        stat.setApp(hitDto.getApp());
        stat.setTimestamp(hitDto.getTimestamp());
        return stat;
    }

    public static StatsDto toStatsDto(Object[] obj) {
        StatsDto statsDto = new StatsDto();
        statsDto.setApp((String) obj[0]);
        statsDto.setUri((String) obj[1]);
        statsDto.setHits((long) obj[2]);
        return statsDto;
    }
}
