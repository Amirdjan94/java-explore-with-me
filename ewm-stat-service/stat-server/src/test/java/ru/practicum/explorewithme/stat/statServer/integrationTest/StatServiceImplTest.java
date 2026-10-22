package ru.practicum.explorewithme.stat.statServer.integrationTest;

import dto.HitDto;
import dto.StatsDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stat.StatApp;
import ru.practicum.stat.model.Stat;
import ru.practicum.stat.service.StatService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5434/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = StatApp.class)
public class StatServiceImplTest {

    private final EntityManager em;
    private final StatService statService;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    HitDto hitDto;
    StatsDto statsDto;

    @BeforeEach
    void setUp() {

    }

    @Test
    void createBooking() {
        HitDto hitDto = makeHitDto("app", "/events", "45.45.45", LocalDateTime.now());
        statService.createStat(hitDto);
        TypedQuery<Stat> query = em.createQuery("Select s from Stat s where s.ip = :ip", Stat.class);
        Stat stat = query.setParameter("ip", "45.45.45")
                .getSingleResult();

        assertThat(stat.getId(), notNullValue());
        assertThat(stat.getApp(), equalTo("app"));
        assertThat(stat.getIp(), equalTo("45.45.45"));
        assertThat(stat.getUri(), equalTo("/events"));
    }

    @Test
    void getAllStatsByParamNotUnique() {

        HitDto hitDto = makeHitDto("app", "/events", "45.45.45", LocalDateTime.now());
        statService.createStat(hitDto);
        statService.createStat(hitDto);

        LocalDateTime end = LocalDateTime.parse("2035-05-05 20:00:00", formatter);
        LocalDateTime start = LocalDateTime.parse("2020-05-05 20:00:00", formatter);
        List<StatsDto> listOfStat = statService.getAllStatsByParam(
                start,
                end,
                List.of("/events"),
                false);
        System.out.println("LIST!!! - " + listOfStat);
        assertThat(listOfStat.get(0).getApp(), equalTo("app"));
        assertThat(listOfStat.get(0).getHits(), equalTo(2L));
        assertThat(listOfStat.get(0).getUri(), equalTo("/events"));
    }

    @Test
    void getAllStatsByParamUnique() {

        HitDto hitDto = makeHitDto("app", "/events", "45.45.45", LocalDateTime.now());
        statService.createStat(hitDto);
        statService.createStat(hitDto);

        LocalDateTime end = LocalDateTime.parse("2035-05-05 20:00:00", formatter);
        LocalDateTime start = LocalDateTime.parse("2020-05-05 20:00:00", formatter);
        List<StatsDto> listOfStat = statService.getAllStatsByParam(
                start,
                end,
                List.of("/events"),
                true);
        System.out.println("LIST!!! - " + listOfStat);
        assertThat(listOfStat.get(0).getApp(), equalTo("app"));
        assertThat(listOfStat.get(0).getHits(), equalTo(1L));
        assertThat(listOfStat.get(0).getUri(), equalTo("/events"));
    }

    private HitDto makeHitDto(String app, String uri, String ip, LocalDateTime localDateTime) {
        return new HitDto(
                app,
                uri,
                ip,
                localDateTime
        );
    }

}
