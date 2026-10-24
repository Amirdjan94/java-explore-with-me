package ru.practicum.ewm.admin.category.service;

import ru.practicum.ewm.admin.category.dto.CategoryDto;
import ru.practicum.ewm.admin.category.dto.NewCategoryDto;

import java.util.Map;

public interface CategoryService {
    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    void deleteCategory(Long categoryId);

    CategoryDto updateCategory(Long categoryId, Map<String, String> newNameMap);
}

