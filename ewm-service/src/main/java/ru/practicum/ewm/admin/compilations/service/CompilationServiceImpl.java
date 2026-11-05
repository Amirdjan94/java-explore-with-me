package ru.practicum.ewm.admin.compilations.service;

import client.StatClient;
import dto.StatsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.admin.compilations.dto.CompilationDto;
import ru.practicum.ewm.admin.compilations.dto.NewCompilationDto;
import ru.practicum.ewm.admin.compilations.dto.UpdateCompilationDto;
import ru.practicum.ewm.admin.compilations.mapper.CompilationMapper;
import ru.practicum.ewm.admin.compilations.model.Compilation;
import ru.practicum.ewm.admin.compilations.repository.CompilationRepository;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.privatePart.events.dto.EventShortDTO;
import ru.practicum.ewm.privatePart.events.mapper.EventMapper;
import ru.practicum.ewm.privatePart.events.model.Event;
import ru.practicum.ewm.privatePart.events.repository.EventRepository;
import ru.practicum.ewm.privatePart.requests.data.RequestStatus;
import ru.practicum.ewm.privatePart.requests.model.Request;
import ru.practicum.ewm.privatePart.requests.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private CompilationRepository compilationRepository;
    private EventRepository eventRepository;
    private RequestRepository requestRepository;
    private StatClient statClient;

    public CompilationServiceImpl(@Qualifier("compilationRepository") CompilationRepository compilationRepository,
                                  @Qualifier("eventRepository") EventRepository eventRepository,
                                  @Qualifier("requestRepository") RequestRepository requestRepository,
                                  StatClient statClient) {
        this.compilationRepository = compilationRepository;
        this.eventRepository = eventRepository;
        this.requestRepository = requestRepository;
        this.statClient = statClient;

    }

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {

        log.info("Собираем список событий");
        List<Event> eventList;
        if (newCompilationDto.getEvents() != null) {
            eventList = eventRepository.findAllById(newCompilationDto.getEvents()); // Получаем список событий
        } else {
            eventList = List.of();
        }

        Set<Event> eventSet = new HashSet<>(eventList); // Создали Set, набор уникальных событий

        log.info("Сохраняем в БД подборку");
        Compilation compilation = CompilationMapper.toCompilationFromNewCompilationDto(newCompilationDto, eventSet);
        Compilation newCompilation = compilationRepository.save(compilation);

        log.info("Собираем информацию по запросам на участие в событии и формируем ответ для пользователя");
        List<Long> eventIds = getEventIds(eventList);  // Получили список ID событий
        Map<Long, Long> requestMap = getRequestMapByEventIds(eventIds); // Собрали мапу: ключ - id события; значение - количество запросов
        List<EventShortDTO> eventShortDTOList = getListEventShortDTO(eventList, eventIds, requestMap); // Соббрали список EventShortDTO для того, чтобы положить в итоговый объект

        return CompilationMapper.toCompilationDtoFromCompilationEntity(newCompilation, eventShortDTOList);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        log.info("Проверяем наличие подборки события в БД с ID - {} ", compId);
        checkExistCompilationAndGetCompEntity(compId);
        compilationRepository.deleteById(compId);
        log.info("Удалена подборки с ID - {} ", compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationDto updateCompilationDto) {

        log.info("Проверяем наличие подборки события в БД с ID - {} ", compId);
        Compilation compilation = checkExistCompilationAndGetCompEntity(compId);

        log.info("Проверяем входные данные и обновляем запись - {} ", updateCompilationDto);
        Compilation updatedCompilation = checkAndUpdateCompilation(compilation, updateCompilationDto);

        log.info("Собираем информацию по запросам на участие в событии и формируем ответ для пользователя");
        List<Event> eventList = new ArrayList<>(updatedCompilation.getEvents()); // Получаем список событий
        List<Long> eventIds = getEventIds(eventList);  // Получили список ID событий
        Map<Long, Long> requestMap = getRequestMapByEventIds(eventIds); // Собрали мапу: ключ - id события; значение - количество запросов
        List<EventShortDTO> eventShortDTOList = getListEventShortDTO(eventList, eventIds, requestMap); // Соббрали список EventShortDTO для того, чтобы положить в итоговый объект

        return CompilationMapper.toCompilationDtoFromCompilationEntity(updatedCompilation, eventShortDTOList);
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        log.info("Проверяем наличие подборки события в БД с ID - {} ", compId);
        Compilation compilation = checkExistCompilationAndGetCompEntity(compId);

        log.info("Собираем список событий");
        List<Event> eventList = compilation.getEvents().stream().toList();

        log.info("Собираем информацию по запросам на участие в событии и формируем ответ для пользователя");
        List<Long> eventIds = getEventIds(eventList);  // Получили список ID событий
        Map<Long, Long> requestMap = getRequestMapByEventIds(eventIds); // Собрали мапу: ключ - id события; значение - количество запросов
        List<EventShortDTO> eventShortDTOList = getListEventShortDTO(eventList, eventIds, requestMap); // Соббрали список EventShortDTO для того, чтобы положить в итоговый объект

        return CompilationMapper.toCompilationDtoFromCompilationEntity(compilation, eventShortDTOList);
    }

    @Override
    public List<CompilationDto> getCompilationList(Boolean pinned, PageRequest pageRequest) {

        log.info("Получили список подборок событий по переданным фильтрам");
        Page<Compilation> compilationPage = compilationRepository.findCompilationByPinned(pinned, pageRequest);

        log.info("Формируем полный список идентификаторов уникальных событий, которые входят в список подборок");
        Set<Long> eventIdSet = new HashSet<>();
        for (Compilation compilation : compilationPage) {
            System.out.println("Compilation compilation = " + compilation);
            eventIdSet.addAll(compilation.getEvents().stream().map(Event::getId).collect(Collectors.toSet()));
        }
        System.out.println("compilationPage compilationPage = " + compilationPage.get());
        System.out.println("Set<Long> eventIdSet = " + eventIdSet);

        log.info("Формируем список событий");
        List<Event> eventList = eventRepository.findAllById(eventIdSet);
        Map<Long, Long> requestMap = getRequestMapByEventIds(new ArrayList<>(eventIdSet)); // Собрали мапу: ключ - id события; значение - количество запросов
        List<EventShortDTO> eventShortDTOList = getListEventShortDTO(eventList, new ArrayList<>(eventIdSet), requestMap); // Соббрали список EventShortDTO для того, чтобы положить в итоговый объект
        System.out.println("Map<Long, Long> requestMap = " + requestMap);

        log.info("Формируем список подборок");
        List<CompilationDto> compilationDtoList = compilationPage.get().map((compilation) ->
                        CompilationMapper.toCompilationDtoFromCompilationEntity(compilation,
                                eventShortDTOList.stream().filter((el) -> // Далее получаем лист ID-ков event
                                                compilation.getEvents()
                                                        .stream()
                                                        .map(Event::getId)
                                                        .toList()
                                                        .contains(el.getId())) // Тут уже проверяем условие
                                        .collect(Collectors.toList())))
                .collect(Collectors.toList());

        return compilationDtoList;
    }

    private List<Long> getEventIds(List<Event> eventList) {
        return eventList.stream().map((el) -> el.getId()).collect(Collectors.toList());
    }

    private List<EventShortDTO> getListEventShortDTO(List<Event> eventList, List<Long> eventIds, Map<Long, Long> requestMap) {
        Map<Long, Long> viewMap = getViewMapByEventIds(eventIds); // Собрали мапу: ключ - id события; значение - количество просмотров
        return eventList.stream()
                .map((el) -> EventMapper.toEventShortDTOFromEventEntity(
                        el,
                        requestMap.get(el.getId()) == null ? 0 : requestMap.get(el.getId()),
                        viewMap.get(el.getId()) == null ? 0 : viewMap.get(el.getId()))
                )
                .collect(Collectors.toList());
    }

    private Map<Long, Long> getViewMapByEventIds(List<Long> eventIds) {
        List<String> urisList = eventIds.stream()
                .map(el -> "/events/" + el)
                .collect(Collectors.toList());
        Map<String, Object> statPayload = new HashMap<>();
        statPayload.put("uris", urisList);
        statPayload.put("unique", "true");
        statPayload.put("start", LocalDateTime.now().minusYears(30));
        statPayload.put("end", LocalDateTime.now().plusYears(30));
        ResponseEntity<List<StatsDto>> statList = statClient.getAllStats(statPayload);
        Map<Long, Long> viewMap = new HashMap<>();
        for (Long eventId : eventIds) {
            Long hitCount = 0L;
            if (!statList.getBody().isEmpty()) {
                hitCount = statList.getBody().stream().filter(statsDto -> statsDto.getUri().equals("/events/" + eventId))
                        .collect(Collectors.toList()).getFirst().getHits();
            }
            viewMap.put(
                    eventId,
                    hitCount != null ? hitCount : 0
            );
        }
        return viewMap;
    }

    private Compilation checkExistCompilationAndGetCompEntity(Long compId) {
        Optional<Compilation> compilation = compilationRepository.findById(compId);
        if (compilation.isEmpty()) {
            log.warn("Подборки с указанным ID - {} не существует ", compId);
            throw new ObjectNotFoundException("Compilation with id=" + compId + " was not found");
        }
        return compilation.get();
    }

    private Map<Long, Long> getRequestMapByEventIds(List<Long> eventIds) {
        List<Request> requestList = requestRepository.findAllRequestByEventIds(eventIds, RequestStatus.CONFIRMED);
        Map<Long, Long> requestMap = new HashMap<>();
        for (Request request : requestList) {
            Long key = request.getEvent().getId();
            if (requestMap.containsKey(key)) {
                requestMap.put(
                        key,
                        requestMap.get(request.getEvent().getId()) + 1
                );
            } else {
                requestMap.put(
                        key,
                        1L
                );
            }
        }
        return requestMap;
    }

    private Compilation checkAndUpdateCompilation(Compilation compilation, UpdateCompilationDto updateCompilationDto) {
        if (updateCompilationDto.getPinned() != null) {
            compilation.setPinned(
                    updateCompilationDto.getPinned()
            );
        }
        if (updateCompilationDto.getTitle() != null) {
            compilation.setTitle(
                    updateCompilationDto.getTitle()
            );
        }
        if (updateCompilationDto.getEvents() != null) {
            List<Event> eventList = eventRepository.findAllById(updateCompilationDto.getEvents()); // Получаем список событий
            compilation.setEvents(
                    new HashSet<>(eventList)
            );
        }

        return compilationRepository.save(compilation);
    }

}
