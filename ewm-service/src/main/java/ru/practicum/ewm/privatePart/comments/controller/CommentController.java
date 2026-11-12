package ru.practicum.ewm.privatePart.comments.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.privatePart.comments.dto.CommentDto;
import ru.practicum.ewm.privatePart.comments.dto.FullCommentDto;
import ru.practicum.ewm.privatePart.comments.dto.NewCommentDto;
import ru.practicum.ewm.privatePart.comments.dto.UpdateCommentDto;
import ru.practicum.ewm.privatePart.comments.service.CommentService;
import ru.practicum.ewm.utils.OffsetPageRequest;

import java.util.List;

@RestController
@RequestMapping("/users")
@Validated
@Slf4j
public class CommentController {
    CommentService commentService;

    public CommentController(@Qualifier("commentServiceImpl") CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/{userId}/events/{eventId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@Valid @RequestBody NewCommentDto newCommentDto,
                                    @PathVariable(name = "userId") Long userId,
                                    @PathVariable(name = "eventId") Long eventId) {
        log.info("Поступил запрос на добавление нового комментария - {} ", newCommentDto);
        return commentService.createComment(newCommentDto, userId, eventId);
    }

    @DeleteMapping("/{userId}/comment/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable(name = "userId") Long userId,
                              @PathVariable(name = "commentId") Long commentId) {
        log.info("Поступил запрос на удаление комментария с ID - {} ", commentId);
        commentService.deleteComment(userId, commentId);
    }

    @PatchMapping("/{userId}/comment/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateComment(@Valid @RequestBody UpdateCommentDto updateCommentDto,
                                    @PathVariable(name = "userId") Long userId,
                                    @PathVariable(name = "commentId") Long commentId) {
        log.info("Поступил запрос на обновлении комментария с ИД - {}", commentId);
        return commentService.updateComment(updateCommentDto, userId, commentId);
    }

    @GetMapping("/{userId}/comment/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public FullCommentDto getCommentById(@PathVariable(name = "userId") Long userId,
                                         @PathVariable(name = "commentId") Long commentId) {
        log.info("Поступил запрос на получение комментария по его Id, добавленного текущим пользователем");
        return commentService.getCommentById(userId, commentId);
    }

    @GetMapping("/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getCommentListByEventId(@PathVariable(name = "eventId") Long eventId,
                                                    @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
                                                    @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        log.info("Поступил запрос, на получении списка опубликованных комментариев события с ИД - {} ", eventId);
        final PageRequest pageRequest = OffsetPageRequest.of(from, size);
        return commentService.getCommentListByEventId(eventId, pageRequest);
    }
}
