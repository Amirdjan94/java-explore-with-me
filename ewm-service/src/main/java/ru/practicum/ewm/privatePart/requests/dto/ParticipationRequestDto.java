package ru.practicum.ewm.privatePart.requests.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.privatePart.requests.data.RequestStatus;

import java.time.LocalDateTime;

@Component
@Data
public class ParticipationRequestDto {

    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created; // Дата и время создания заявки
    private Long event; // Идентификатор события
    private Long requester; // Идентификатор пользователя подавшего запрос
    private RequestStatus status; // Статус заявки
}
