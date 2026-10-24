package ru.practicum.ewm.admin.users.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name = "users", schema = "public")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // уникальный идентификатор пользователя

    @Column(name = "email", unique = true, nullable = false, length = 254)
    private String email; // адрес электронной почты

    @Column(name = "name", length = 250, nullable = false)
    private String name; // имя или логин пользователя
}
