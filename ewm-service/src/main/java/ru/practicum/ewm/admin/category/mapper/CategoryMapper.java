package ru.practicum.ewm.admin.category.mapper;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.admin.category.dto.CategoryDto;
import ru.practicum.ewm.admin.category.dto.NewCategoryDto;
import ru.practicum.ewm.admin.category.model.Category;

@Component
@NoArgsConstructor
public class CategoryMapper {
    public static Category toCategoryFromNewCategoryDto(NewCategoryDto newCategoryDto) {
        Category category = new Category();
        category.setName(newCategoryDto.getName());
        return category;
    }

    public static CategoryDto toCategoryDtoFromCategory(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        return categoryDto;
    }
}
