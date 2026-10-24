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
    private String annotation;

    @Column(name = "confirmed_requests")
    private Long confirmedRequests;

    @Column(name = "description", nullable = false, length = 7000)
    private String description;

    @Column(name = "paid")
    private Boolean paid;

    @Column(name = "participant_limit")
    private Integer participantLimit;

    @Column(name = "request_moderation")
    private Boolean requestModeration;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private State state;

    @Column(name = "title", nullable = false, length = 120)
    private String title;

    @Column(name = "views")
    private Long views;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Embedded
    private Location location;

}