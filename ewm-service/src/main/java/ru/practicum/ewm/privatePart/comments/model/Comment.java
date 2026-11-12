package ru.practicum.ewm.privatePart.comments.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.admin.users.model.User;
import ru.practicum.ewm.privatePart.comments.data.CommentStatus;
import ru.practicum.ewm.privatePart.events.model.Event;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "comments", schema = "public")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "text", length = 7000)
    String text;

    @ManyToOne
    @JoinColumn(name = "event_id")
    Event event;

    @ManyToOne
    @JoinColumn(name = "author_id")
    User user;

    @Column(name = "created", nullable = false)
    LocalDateTime created;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CommentStatus commentStatus;
}
