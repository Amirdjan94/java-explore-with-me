package ru.practicum.stat.service;

import dto.HitDto;
import dto.StatsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.stat.mapper.StatMapper;
import ru.practicum.stat.model.Stat;
import ru.practicum.stat.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StatServiceImpl implements StatService {
    StatRepository statRepository;

    public StatServiceImpl(StatRepository statRepository) {
        this.statRepository = statRepository;
    }

    @Override
    public void createStat(HitDto hitDto) {
        Stat stat = StatMapper.toStat(hitDto);
        statRepository.save(stat);
        log.info("Информация успешно сохранена - {}", hitDto);
    }

    @Override
    public List<StatsDto> getAllStatsByParam(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        List<Object[]> statList;
        if (unique) {
            statList = statRepository.getAllStatsByParamAndUnique(start, end, uris);
        } else {
            statList = statRepository.getAllStatsByParam(start, end, uris);
        }
        return statList.stream().map(StatMapper::toStatsDto).collect(Collectors.toList());
    }
}
