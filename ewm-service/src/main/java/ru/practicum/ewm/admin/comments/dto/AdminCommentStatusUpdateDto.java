package ru.practicum.ewm.admin.comments.dto;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.admin.comments.data.CommentStateAction;

@Data
@Component
public class AdminCommentStatusUpdateDto {
    CommentStateAction status;
}
