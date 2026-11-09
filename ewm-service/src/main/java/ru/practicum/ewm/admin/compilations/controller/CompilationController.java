package ru.practicum.ewm.admin.compilations.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.admin.compilations.dto.CompilationDto;
import ru.practicum.ewm.admin.compilations.dto.NewCompilationDto;
import ru.practicum.ewm.admin.compilations.dto.UpdateCompilationDto;
import ru.practicum.ewm.admin.compilations.service.CompilationService;

@RestController
@RequestMapping("/admin/compilations")
@Validated
@Slf4j
public class CompilationController {

    private CompilationService compilationService;

    public CompilationController(@Qualifier("compilationServiceImpl") CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("Поступил запрос на создание подборки событий {} ", newCompilationDto);
        return compilationService.createCompilation(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable(name = "compId") Long compId) {
        log.info("Поступил запрос на удаление подборки событий с ИД - {} ", compId);
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto updateCategory(@PathVariable(name = "compId") Long compId,
                                         @Valid @RequestBody UpdateCompilationDto updateCompilationDto) {
        log.info("Поступил запрос на обновлении подборки событий с ИД - {}, на - {}", compId, updateCompilationDto);
        return compilationService.updateCompilation(compId, updateCompilationDto);
    }
}
