package ru.practicum.ewm.privatePart.comments.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.admin.comments.data.CommentStateAction;
import ru.practicum.ewm.admin.comments.dto.AdminCommentStatusUpdateDto;
import ru.practicum.ewm.admin.users.model.User;
import ru.practicum.ewm.admin.users.repository.UserRepository;
import ru.practicum.ewm.exception.ConditionsNotMetException;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.privatePart.comments.data.CommentStatus;
import ru.practicum.ewm.privatePart.comments.dto.CommentDto;
import ru.practicum.ewm.privatePart.comments.dto.FullCommentDto;
import ru.practicum.ewm.privatePart.comments.dto.NewCommentDto;
import ru.practicum.ewm.privatePart.comments.dto.UpdateCommentDto;
import ru.practicum.ewm.privatePart.comments.mapper.CommentMapper;
import ru.practicum.ewm.privatePart.comments.model.Comment;
import ru.practicum.ewm.privatePart.comments.repository.CommentRepository;
import ru.practicum.ewm.privatePart.events.model.Event;
import ru.practicum.ewm.privatePart.events.repository.EventRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private EventRepository eventRepository;
    private UserRepository userRepository;
    private CommentRepository commentRepository;

    public CommentServiceImpl(@Qualifier("commentRepository") CommentRepository commentRepository,
                              @Qualifier("eventRepository") EventRepository eventRepository,
                              @Qualifier("userRepository") UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public CommentDto createComment(NewCommentDto newCommentDto, Long userId, Long eventId) {
        log.info("Проверяем наличие пользователя в БД с ID - {} ", userId);
        User user = checkUserIdAndGetUserEntity(userId);
        log.info("Проверяем наличие события в БД с ID - {} ", eventId);
        Event event = checkEventIdAndGetEventEntity(eventId);
        checkBusinessRuleForCreateComment(user, event);
        Comment comment = CommentMapper.toCommentFromNewCommentDto(newCommentDto, user, event);
        Comment newComment = commentRepository.save(comment);
        log.info("Создали новый комментарий и сохранили в БД - {} ", newComment);
        return CommentMapper.toCommentDtoFromCommentEntity(newComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        log.info("Проверяем наличие пользователя в БД с ID - {} ", userId);
        User user = checkUserIdAndGetUserEntity(userId);
        log.info("Проверяем наличие комментария в БД с ID - {} ", commentId);
        Comment comment = checkCommentIdAndGetCommentEntity(commentId);
        if (!comment.getUser().getId().equals(userId)) {
            log.warn("Только владелец комментария может его удалить");
            throw new DataIntegrityViolationException("Only the author of a comment can delete it");
        }
        commentRepository.deleteById(commentId);
        log.info("Удален комментарий с ID - {} ", commentId);
    }

    @Override
    @Transactional
    public CommentDto updateComment(UpdateCommentDto updateCommentDto, Long userId, Long commentId) {
        log.info("Проверяем наличие пользователя в БД с ID - {} ", userId);
        User user = checkUserIdAndGetUserEntity(userId);
        log.info("Проверяем наличие комментария в БД с ID - {} ", commentId);
        Comment comment = checkCommentIdAndGetCommentEntity(commentId);
        if (!comment.getUser().getId().equals(userId)) {
            log.warn("Только владелец комментария может его обновить");
            throw new DataIntegrityViolationException("Only the author of a comment can updated it");
        }
        comment.setText(updateCommentDto.getText());
        Comment newComment = commentRepository.save(comment);
        log.info("Обновили комментарий и сохранили в БД - {} ", newComment);
        return CommentMapper.toCommentDtoFromCommentEntity(newComment);
    }

    @Override
    public FullCommentDto getCommentById(Long userId, Long commentId) {
        log.info("Проверяем наличие комментария в БД с ID - {} ", commentId);
        Comment comment = checkCommentIdAndGetCommentEntity(commentId);
        if (!comment.getUser().getId().equals(userId)) {
            log.warn("Только владелец комментария может получить о нем полную информацию");
            throw new DataIntegrityViolationException("Only the author of a comment can access full information about it");
        }
        return CommentMapper.toFullCommentDtoFromCommentEntity(comment);
    }

    @Override
    public List<CommentDto> getCommentListByEventId(Long eventId, PageRequest pageRequest) {
        log.info("Проверяем наличие события в БД с ID - {} ", eventId);
        checkEventIdAndGetEventEntity(eventId);
        Page<Comment> commentList = commentRepository.getCommentListByCommentStatus(eventId, CommentStatus.PUBLISHED, pageRequest);
        return commentList.stream().map(CommentMapper::toCommentDtoFromCommentEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FullCommentDto updateCommentStatus(AdminCommentStatusUpdateDto adminCommentStatusUpdateDto, Long commentId) {
        log.info("Проверяем наличие комментария в БД с ID - {} ", commentId);
        Comment comment = checkCommentIdAndGetCommentEntity(commentId);
        Comment newComment = checkAndUpdateCommentStatus(adminCommentStatusUpdateDto, comment);
        log.info("Обновили комментарий и сохранили в БД - {} ", newComment);
        return CommentMapper.toFullCommentDtoFromCommentEntity(newComment);
    }

    @Override
    public List<FullCommentDto> getCommentListByEventIdForAdmin(Long eventId, CommentStatus commentStatus, PageRequest pageRequest) {
        log.info("Проверяем наличие события в БД с ID - {} ", eventId);
        checkEventIdAndGetEventEntity(eventId);
        Page<Comment> commentList = commentRepository.getCommentListByCommentStatus(eventId, commentStatus, pageRequest);
        return commentList.stream().map(CommentMapper::toFullCommentDtoFromCommentEntity).collect(Collectors.toList());
    }

    private Comment checkAndUpdateCommentStatus(AdminCommentStatusUpdateDto adminCommentStatusUpdateDto, Comment comment) {

        switch (adminCommentStatusUpdateDto.getStatus()) {
            case CommentStateAction.PUBLISH_COMMENT:
                comment.setCommentStatus(CommentStatus.PUBLISHED);
                return commentRepository.save(comment);
            case CommentStateAction.REJECT_COMMENT:
                comment.setCommentStatus(CommentStatus.REJECTED);
                return commentRepository.save(comment);
            default:
                throw new ConditionsNotMetException("Incorrect admin comment status");
        }
    }

    private Comment checkCommentIdAndGetCommentEntity(Long commentId) {
        if (commentId == null || commentId <= 0) {
            throw new ConditionsNotMetException("Incorrect commentId");
        }
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty()) {
            log.warn("Комментария с указанным ID - {} не существует ", commentId);
            throw new ObjectNotFoundException("Comment with id=" + commentId + " was not found");
        }
        return comment.get();
    }

    private User checkUserIdAndGetUserEntity(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ConditionsNotMetException("Incorrect userId");
        }
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            log.warn("Пользователя с указанным ID - {} не существует ", userId);
            throw new ObjectNotFoundException("User with id=" + userId + " was not found");
        }
        return user.get();
    }

    private Event checkEventIdAndGetEventEntity(Long eventId) {
        if (eventId == null || eventId <= 0) {
            log.warn("Не корректное Id события - " + eventId);
            throw new ConditionsNotMetException("Incorrect event Id");
        }
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
            log.warn("События с указанным ID - {} не существует ", eventId);
            throw new ObjectNotFoundException("Event with id=" + eventId + " was not found");
        }
        return event.get();
    }

    private void checkBusinessRuleForCreateComment(User user, Event event) {
        if (event.getUser().getId().equals(user.getId())) {
            log.warn("Владелец события не может комментировать свое событие");
            throw new DataIntegrityViolationException("The event owner cannot comment on their own event");
        }
    }
}
