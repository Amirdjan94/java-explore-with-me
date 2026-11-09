package ru.practicum.ewm.privatePart.requests.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.admin.users.model.User;
import ru.practicum.ewm.privatePart.events.model.Event;
import ru.practicum.ewm.privatePart.requests.data.RequestStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@Table(name = "requests", schema = "public")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "created", nullable = false)
    private LocalDateTime created; // Дата и время создания заявки

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event; // Идентификатор события

    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private User user;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus; // Статус заявки

}
