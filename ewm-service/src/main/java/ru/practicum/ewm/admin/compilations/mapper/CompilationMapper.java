package ru.practicum.ewm.admin.compilations.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.admin.compilations.dto.CompilationDto;
import ru.practicum.ewm.admin.compilations.dto.NewCompilationDto;
import ru.practicum.ewm.admin.compilations.model.Compilation;
import ru.practicum.ewm.privatePart.events.dto.EventShortDTO;
import ru.practicum.ewm.privatePart.events.model.Event;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class CompilationMapper {
    public static Compilation toCompilationFromNewCompilationDto(NewCompilationDto newCompilationDto, Set<Event> eventsSet) {
        Compilation compilation = new Compilation();
        compilation.setEvents(eventsSet);
        compilation.setTitle(newCompilationDto.getTitle());
        compilation.setPinned(newCompilationDto.getPinned());
        return compilation;
    }

    public static CompilationDto toCompilationDtoFromCompilationEntity(Compilation compilation, List<EventShortDTO> eventShortDTOList) {
        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setId(compilation.getId());
        compilationDto.setEvents(new HashSet<>(eventShortDTOList));
        compilationDto.setTitle(compilation.getTitle());
        compilationDto.setPinned(compilation.getPinned());
        return compilationDto;
    }
}
