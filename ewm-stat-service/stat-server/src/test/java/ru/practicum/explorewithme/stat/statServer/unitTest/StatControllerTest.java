package ru.practicum.explorewithme.stat.statServer.unitTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.HitDto;
import dto.StatsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.stat.StatApp;
import ru.practicum.stat.controller.StatController;
import ru.practicum.stat.service.StatService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = StatController.class)
@ContextConfiguration(classes = StatApp.class)
public class StatControllerTest {
    @MockBean
    @Qualifier("statServiceImpl")
    StatService statService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    HitDto hitDto;
    StatsDto statsDto;
    LocalDateTime localDateTime = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        hitDto = new HitDto(
                "app",
                "/events",
                "45.45.45",
                localDateTime
        );

        statsDto = new StatsDto(
                "app",
                "/events",
                1L
        );
    }

    @Test
    void createStat() throws Exception {

        mvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(hitDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string("Информация сохранена"));
    }

    @Test
    void getAllStatsByParam() throws Exception {

        when(statService.getAllStatsByParam(any(), any(), any(), any()))
                .thenReturn(List.of(statsDto));

        mvc.perform(get("/stats?start=" + localDateTime + "&end=" + localDateTime + "&uris=/events")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].app", is(statsDto.getApp())))
                .andExpect(jsonPath("$[0].uri", is(statsDto.getUri())))
                .andExpect(jsonPath("$[0].hits", is(statsDto.getHits()), Long.class));
    }
}