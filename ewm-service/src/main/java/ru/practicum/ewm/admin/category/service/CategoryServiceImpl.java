package ru.practicum.ewm.admin.category.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.admin.category.dto.CategoryDto;
import ru.practicum.ewm.admin.category.dto.NewCategoryDto;
import ru.practicum.ewm.admin.category.mapper.CategoryMapper;
import ru.practicum.ewm.admin.category.model.Category;
import ru.practicum.ewm.admin.category.repository.CategoryRepository;
import ru.practicum.ewm.exception.ConditionsNotMetException;
import ru.practicum.ewm.exception.ObjectNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    CategoryRepository categoryRepository;

    public CategoryServiceImpl(@Qualifier("categoryRepository") CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        Category category = CategoryMapper.toCategoryFromNewCategoryDto(newCategoryDto);
        Category newCategory = categoryRepository.save(category);
        log.info("Создали новую категорию и сохранили в БД - {} ", newCategory);
        return CategoryMapper.toCategoryDtoFromCategory(newCategory);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        log.info("Проверяем наличие категории в БД с ID - {} ", categoryId);
        checkExistCategoryAndGetCategoryEntity(categoryId);
        categoryRepository.deleteById(categoryId);
        log.info("Удалена категория с ID - {} ", categoryId);
    }

    @Override
    public CategoryDto updateCategory(Long categoryId, Map<String, String> newNameMap) {
        log.info("Проверяем наличие категории в БД с ID - {} ", categoryId);
        checkExistCategoryAndGetCategoryEntity(categoryId);
        log.info("Проверяем входные данные - {} ", newNameMap);
        checkNewNameMap(newNameMap);
        Category category = categoryRepository.findById(categoryId).get();
        category.setName(newNameMap.get("name"));
        Category newCategory = categoryRepository.save(category);
        log.info("Обновили данные и сохранили в БД - {} ", newCategory);
        return CategoryMapper.toCategoryDtoFromCategory(newCategory);
    }

    @Override
    public List<CategoryDto> getCategoryList(PageRequest pageRequest) {
        return categoryRepository.findAll(pageRequest).stream().map(CategoryMapper::toCategoryDtoFromCategory)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long categoryId) {
        log.info("Проверяем наличие категории в БД с ID - {} ", categoryId);
        Category category = checkExistCategoryAndGetCategoryEntity(categoryId);
        return CategoryMapper.toCategoryDtoFromCategory(category);
    }

    private Category checkExistCategoryAndGetCategoryEntity(Long categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()) {
            log.warn("Категории с указанным ID - {} не существует ", categoryId);
            throw new ObjectNotFoundException("Category with id=" + categoryId + " was not found");
        }
        return category.get();
    }

    private void checkNewNameMap(Map<String, String> newNameMap) {
        if (newNameMap == null || newNameMap.isEmpty()) {
            log.warn("Payload не должен быть пустым");
            throw new ConditionsNotMetException("Payload is not to be empty");
        }
        if (newNameMap.size() != 1 || !newNameMap.containsKey("name") || newNameMap.get("name").isEmpty()
                || newNameMap.get("name").isBlank()) {
            log.warn("Не корректные данные в Payload");
            throw new ConditionsNotMetException("Incorrect payload data");
        }
        if (newNameMap.get("name").length() > 50) {
            log.warn("Количество символов в поле name превышает 50");
            throw new ConditionsNotMetException("Name field length is not to be greater than 50");
        }
    }
}
