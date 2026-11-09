package ru.practicum.ewm.privatePart.events.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.admin.category.model.Category;
import ru.practicum.ewm.admin.users.model.User;
import ru.practicum.ewm.privatePart.events.data.State;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@Table(name = "events", schema = "public")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "annotation", length = 2000, nullable = false)
    private String annotation; // Краткое описание

    @Column(name = "description", nullable = false, length = 7000)
    private String description; // Полное описание события

    @Column(name = "paid")
    private Boolean paid; // Нужно ли оплачивать участие

    @Column(name = "participant_limit")
    private Integer participantLimit; // Ограничение на количество участников. Значение 0 - означает отсутствие ограничения

    @Column(name = "request_moderation")
    private Boolean requestModeration; // Нужна ли пре-модерация заявок на участие

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private State state; // Список состояний жизненного цикла события

    @Column(name = "title", nullable = false, length = 120)
    private String title; // Заголовок

    @Column(name = "views")
    private Long views; // Количество просмотрев события [PENDING, PUBLISHED, CANCELED]

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate; // Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")

    @Column(name = "published_on")
    private LocalDateTime publishedOn; // Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss")

    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Embedded
    private Location location;

}