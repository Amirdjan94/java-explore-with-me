package ru.practicum.ewm.admin.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.admin.category.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
