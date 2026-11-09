package ru.practicum.ewm.admin.category.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name = "categories", schema = "public")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // уникальный идентификатор категории

    @Column(name = "name", unique = true, nullable = false, length = 50)
    private String name; // имя категории
}
