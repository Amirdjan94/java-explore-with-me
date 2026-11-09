package ru.practicum.ewm.admin.compilations.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.admin.compilations.dto.CompilationDto;
import ru.practicum.ewm.admin.compilations.dto.NewCompilationDto;
import ru.practicum.ewm.admin.compilations.dto.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationDto updateCompilationDto);

    List<CompilationDto> getCompilationList(Boolean pinned, PageRequest pageRequest);

    CompilationDto getCompilationById(Long compId);
}
