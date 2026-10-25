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

    @Column(name = "created")
    private LocalDateTime created; // Дата и время создания заявки

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event; // Идентификатор события

    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User user;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus; // Статус заявки

}
