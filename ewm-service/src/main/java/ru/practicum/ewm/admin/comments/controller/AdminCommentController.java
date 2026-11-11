package ru.practicum.ewm.admin.comments.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.admin.comments.dto.AdminCommentStatusUpdateDto;
import ru.practicum.ewm.privatePart.comments.data.CommentStatus;
import ru.practicum.ewm.privatePart.comments.dto.FullCommentDto;
import ru.practicum.ewm.privatePart.comments.service.CommentService;
import ru.practicum.ewm.utils.OffsetPageRequest;

import java.util.List;

@RestController
@RequestMapping("/admin")
@Validated
@Slf4j
public class AdminCommentController {
    CommentService commentService;

    public AdminCommentController(@Qualifier("commentServiceImpl") CommentService commentService) {
        this.commentService = commentService;
    }

    @PatchMapping("/comment/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public FullCommentDto updateCommentStatus(@Valid @RequestBody AdminCommentStatusUpdateDto adminCommentStatusUpdateDto,
                                              @PathVariable(name = "commentId") Long commentId) {
        log.info("Поступил запрос на обновлении комментария с ИД - {}", commentId);
        return commentService.updateCommentStatus(adminCommentStatusUpdateDto, commentId);
    }

    @GetMapping("/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<FullCommentDto> getCommentListByEventIdForAdmin(@PathVariable(name = "eventId") Long eventId,
                                                                @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
                                                                @RequestParam(name = "size", defaultValue = "10", required = false) Integer size,
                                                                @RequestParam(name = "status", required = false) CommentStatus commentStatus
    ) {
        log.info("Поступил запрос, на получении списка опубликованных комментариев события с ИД - {} ", eventId);
        final PageRequest pageRequest = OffsetPageRequest.of(from, size);
        return commentService.getCommentListByEventIdForAdmin(eventId, commentStatus, pageRequest);
    }
}
