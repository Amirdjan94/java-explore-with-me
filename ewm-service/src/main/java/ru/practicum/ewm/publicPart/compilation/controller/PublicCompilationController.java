package ru.practicum.ewm.publicPart.compilation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.admin.compilations.dto.CompilationDto;
import ru.practicum.ewm.admin.compilations.service.CompilationService;
import ru.practicum.ewm.utils.OffsetPageRequest;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@Validated
@Slf4j
public class PublicCompilationController {
    private CompilationService compilationService;

    public PublicCompilationController(@Qualifier("compilationServiceImpl") CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> getCompilationList(@RequestParam(name = "pinned", required = false) Boolean pinned,
                                                   @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
                                                   @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        log.info("Поступил публичный запрос на получение списка подборок");
        final PageRequest pageRequest = OffsetPageRequest.of(from, size);
        return compilationService.getCompilationList(pinned, pageRequest);
    }

    @GetMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto getCompilationById(@PathVariable(name = "compId") Long compId) {
        log.info("Поступил публичный запрос на получение подбороки по ID - {}", compId);
        return compilationService.getCompilationById(compId);
    }
}
