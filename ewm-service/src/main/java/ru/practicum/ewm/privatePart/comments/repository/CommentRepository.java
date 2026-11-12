package ru.practicum.ewm.privatePart.comments.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.privatePart.comments.data.CommentStatus;
import ru.practicum.ewm.privatePart.comments.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE (:status IS NULL OR c.commentStatus  = :status) " +
            " AND  (c.event.id = :eventId) " +
            " ORDER BY c.created DESC")
    Page<Comment> getCommentListByCommentStatus(@Param("eventId") Long eventId,
                                                @Param("status") CommentStatus status,
                                                PageRequest pageRequest);
}
