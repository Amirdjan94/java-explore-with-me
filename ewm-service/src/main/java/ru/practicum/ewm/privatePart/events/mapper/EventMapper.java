package ru.practicum.ewm.privatePart.events.mapper;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.admin.category.model.Category;
import ru.practicum.ewm.admin.users.dto.UserShortDto;
import ru.practicum.ewm.admin.users.mapper.UserMapper;
import ru.practicum.ewm.admin.users.model.User;
import ru.practicum.ewm.privatePart.events.data.State;
import ru.practicum.ewm.privatePart.events.dto.EventFullDTO;
import ru.practicum.ewm.privatePart.events.dto.EventShortDTO;
import ru.practicum.ewm.privatePart.events.dto.NewEventDTO;
import ru.practicum.ewm.privatePart.events.model.Event;

import java.time.LocalDateTime;

@Component
@NoArgsConstructor
public class EventMapper {
    public static Event toEventFronNewEventDto(NewEventDTO newEventDTO, Category category,
                                               User user) {
        Event event = new Event();
        event.setDescription(newEventDTO.getDescription());
        event.setAnnotation(newEventDTO.getAnnotation());
        event.setCategory(category);
        event.setLocation(newEventDTO.getLocation());
        event.setEventDate(newEventDTO.getEventDate());
        event.setPaid(newEventDTO.getPaid());
        event.setParticipantLimit(newEventDTO.getParticipantLimit());
        event.setRequestModeration(newEventDTO.getRequestModeration());
        event.setTitle(newEventDTO.getTitle());
        event.setUser(user);
        event.setViews(0L);
        event.setPublishedOn(null);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(State.PENDING);
        return event;
    }

    public static EventFullDTO toEventFullDTOFromEventEntity(Event event, Long confirmedRequestsCount, Long viewCount) {
        EventFullDTO eventFullDTO = new EventFullDTO();
        UserShortDto userShortDto = UserMapper.toUserShortDtoFromUserEntity(event.getUser());
        eventFullDTO.setId(event.getId());
        eventFullDTO.setAnnotation(event.getAnnotation());
        eventFullDTO.setConfirmedRequests(confirmedRequestsCount);
        eventFullDTO.setDescription(event.getDescription());
        eventFullDTO.setPaid(event.getPaid());
        eventFullDTO.setParticipantLimit(event.getParticipantLimit());
        eventFullDTO.setRequestModeration(event.getRequestModeration());
        eventFullDTO.setState(event.getState());
        eventFullDTO.setTitle(event.getTitle());
        eventFullDTO.setViews(viewCount);
        eventFullDTO.setCreatedOn(event.getCreatedOn());
        eventFullDTO.setEventDate(event.getEventDate());
        eventFullDTO.setPublishedOn(event.getPublishedOn());
        eventFullDTO.setInitiator(userShortDto);
        eventFullDTO.setCategory(event.getCategory());
        eventFullDTO.setLocation(event.getLocation());
        return eventFullDTO;
    }

    public static EventShortDTO toEventShortDTOFromEventEntity(Event event, Long confirmedRequestsCount, Long viewCount) {
        EventShortDTO eventShortDTO = new EventShortDTO();
        UserShortDto userShortDto = UserMapper.toUserShortDtoFromUserEntity(event.getUser());
        eventShortDTO.setId(event.getId());
        eventShortDTO.setAnnotation(event.getAnnotation());
        eventShortDTO.setConfirmedRequests(confirmedRequestsCount);
        eventShortDTO.setPaid(event.getPaid());
        eventShortDTO.setTitle(event.getTitle());
        eventShortDTO.setViews(viewCount);
        eventShortDTO.setEventDate(event.getEventDate());
        eventShortDTO.setInitiator(userShortDto);
        eventShortDTO.setCategory(event.getCategory());
        return eventShortDTO;
    }

}