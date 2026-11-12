package ru.practicum.ewm.privatePart.comments.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.admin.comments.dto.AdminCommentStatusUpdateDto;
import ru.practicum.ewm.privatePart.comments.data.CommentStatus;
import ru.practicum.ewm.privatePart.comments.dto.CommentDto;
import ru.practicum.ewm.privatePart.comments.dto.FullCommentDto;
import ru.practicum.ewm.privatePart.comments.dto.NewCommentDto;
import ru.practicum.ewm.privatePart.comments.dto.UpdateCommentDto;

import java.util.List;

public interface CommentService {

    CommentDto createComment(NewCommentDto newCommentDto, Long userId, Long eventId);

    void deleteComment(Long userId, Long commentId);

    CommentDto updateComment(UpdateCommentDto updateCommentDto, Long userId, Long commentId);

    FullCommentDto getCommentById(Long userId, Long commentId);

    List<CommentDto> getCommentListByEventId(Long eventId, PageRequest pageRequest);

    FullCommentDto updateCommentStatus(AdminCommentStatusUpdateDto adminCommentStatusUpdateDto, Long commentId);

    List<FullCommentDto> getCommentListByEventIdForAdmin(Long eventId, CommentStatus commentStatus, PageRequest pageRequest);

}
