package ru.practicum.ewm.admin.category.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.admin.category.dto.CategoryDto;
import ru.practicum.ewm.admin.category.dto.NewCategoryDto;
import ru.practicum.ewm.admin.category.service.CategoryService;

import java.util.Map;

@RestController
@RequestMapping("/admin/categories")
@Validated
@Slf4j
public class CategoryController {

    private CategoryService categoryService;

    public CategoryController(@Qualifier("categoryServiceImpl") CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("Поступил запрос на добавление новой категории - {} ", newCategoryDto);
        return categoryService.createCategory(newCategoryDto);
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable(name = "categoryId") Long categoryId) {
        log.info("Поступил запрос на удаление категории с ИД - {} ", categoryId);
        categoryService.deleteCategory(categoryId);
    }

    @PatchMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategory(@PathVariable(name = "categoryId") Long categoryId,
                                      @RequestBody Map<String, String> newNameMap) {
        log.info("Поступил запрос на обновлении категории с ИД - {}, на - {}", categoryId, newNameMap);
        return categoryService.updateCategory(categoryId, newNameMap);
    }
}