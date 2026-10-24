package ru.practicum.ewm.privatePart.events.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.privatePart.events.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}
