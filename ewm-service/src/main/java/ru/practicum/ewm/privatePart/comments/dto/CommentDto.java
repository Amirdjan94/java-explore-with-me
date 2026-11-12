package ru.practicum.ewm.privatePart.comments.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Data
@Component
public class CommentDto {

    Long id;
    String text;
    Long eventId;
    Long authorId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime created;

}
