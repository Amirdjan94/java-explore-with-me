package ru.practicum.explorewithme.stat.statdto.unitTest;

import dto.StatsDto;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class StatsDtoTest {
    private final JacksonTester<StatsDto> json;
    private StatsDto statsDto;

    @Test
    void testStatsDto() throws Exception {
        statsDto = new StatsDto(
                "ewm-main-service",
                "/events/1",
                5L
        );

        JsonContent<StatsDto> result = json.write(statsDto);
        assertThat(result).extractingJsonPathStringValue("$.app").isEqualTo("ewm-main-service");
        assertThat(result).extractingJsonPathStringValue("$.uri").isEqualTo("/events/1");
        assertThat(result).extractingJsonPathNumberValue("$.hits").isEqualTo(5);
    }
}
