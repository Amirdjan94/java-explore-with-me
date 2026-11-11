package ru.practicum.ewm.privatePart.comments.mapper;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.admin.users.model.User;
import ru.practicum.ewm.privatePart.comments.data.CommentStatus;
import ru.practicum.ewm.privatePart.comments.dto.CommentDto;
import ru.practicum.ewm.privatePart.comments.dto.FullCommentDto;
import ru.practicum.ewm.privatePart.comments.dto.NewCommentDto;
import ru.practicum.ewm.privatePart.comments.model.Comment;
import ru.practicum.ewm.privatePart.events.model.Event;

import java.time.LocalDateTime;

@Component
@NoArgsConstructor
public class CommentMapper {
    public static Comment toCommentFromNewCommentDto(NewCommentDto newCommentDto,
                                                     User user,
                                                     Event event) {
        Comment comment = new Comment();
        comment.setCommentStatus(CommentStatus.PENDING);
        comment.setUser(user);
        comment.setText(newCommentDto.getText());
        comment.setEvent(event);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    public static CommentDto toCommentDtoFromCommentEntity(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setAuthorId(comment.getUser().getId());
        commentDto.setText(comment.getText());
        commentDto.setEventId(comment.getEvent().getId());
        commentDto.setCreated(comment.getCreated());
        commentDto.setId(comment.getId());
        return commentDto;
    }

    public static FullCommentDto toFullCommentDtoFromCommentEntity(Comment comment) {
        FullCommentDto fullCommentDto = new FullCommentDto();
        fullCommentDto.setAuthorId(comment.getUser().getId());
        fullCommentDto.setText(comment.getText());
        fullCommentDto.setEventId(comment.getEvent().getId());
        fullCommentDto.setCreated(comment.getCreated());
        fullCommentDto.setId(comment.getId());
        fullCommentDto.setCommentStatus(comment.getCommentStatus());
        return fullCommentDto;
    }
}
