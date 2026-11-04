package ru.practicum.ewm.publicPart.category.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.admin.category.dto.CategoryDto;
import ru.practicum.ewm.admin.category.service.CategoryService;
import ru.practicum.ewm.utils.OffsetPageRequest;

import java.util.List;

@RestController
@RequestMapping("/categories")
@Validated
@Slf4j
public class PublicCategoryController {

    private CategoryService categoryService;

    public PublicCategoryController(@Qualifier("categoryServiceImpl") CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getCategoryList(@RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
                                             @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        log.info("Поступил запрос на получение списка категорий");
        final PageRequest pageRequest = OffsetPageRequest.of(from, size);
        return categoryService.getCategoryList(pageRequest);
    }

    @GetMapping("{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategoryById(@PathVariable(name = "categoryId") Long categoryId) {
        log.info("Поступил запрос на получение списка категорий");
        return categoryService.getCategoryById(categoryId);
    }

}
