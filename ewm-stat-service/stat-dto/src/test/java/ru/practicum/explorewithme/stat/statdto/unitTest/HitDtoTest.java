package ru.practicum.explorewithme.stat.statdto.unitTest;

import dto.HitDto;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class HitDtoTest {
    private final JacksonTester<HitDto> json;
    private HitDto hitDto;

    @Test
    void testHitDto() throws Exception {
        hitDto = new HitDto(
                "ewm-main-service",
                "/events/1",
                "192.163.0.1",
                LocalDateTime.now()
        );

        JsonContent<HitDto> result = json.write(hitDto);
        assertThat(result).extractingJsonPathStringValue("$.app").isEqualTo("ewm-main-service");
        assertThat(result).extractingJsonPathStringValue("$.uri").isEqualTo("/events/1");
        assertThat(result).extractingJsonPathStringValue("$.ip").isEqualTo("192.163.0.1");
    }
}
