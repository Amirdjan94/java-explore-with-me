package ru.practicum.ewm.privatePart.events.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.privatePart.events.data.State;
import ru.practicum.ewm.privatePart.events.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e WHERE (:users IS NULL OR e.user.id IN :users) " +
            " AND (:states IS NULL OR e.state IN :states) " +
            " AND (:categories IS NULL OR e.category.id IN :categories) " +
            " AND (cast(:rangeStart as timestamp) IS NULL OR e.eventDate >= :rangeStart) " +
            " AND (cast(:rangeEnd as timestamp) IS NULL OR e.eventDate <= :rangeEnd) " +
            " ORDER BY e.eventDate DESC, e.id DESC")
    Page<Event> findByParams(@Param("users") List<Long> users,
                             @Param("states") List<State> states,
                             @Param("categories") List<Long> categories,
                             @Param("rangeStart") LocalDateTime rangeStart,
                             @Param("rangeEnd") LocalDateTime rangeEnd,
                             PageRequest pageRequest);

    @Query("SELECT e FROM Event e WHERE (e.user.id = :userId) " +
            " ORDER BY e.eventDate DESC, e.id DESC")
    Page<Event> findByUserId(@Param("userId") Long userId,
                             PageRequest pageRequest);

    @Query("SELECT e FROM Event e " +
            " WHERE ((LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) OR :text IS NULL ) " +
            " OR (LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%')) OR :text IS NULL )) " +
            " AND (:categories IS NULL OR e.category.id IN :categories) " +
            " AND (:paid IS NULL OR e.paid IN :paid) " +
            " AND (cast(:rangeStart as timestamp) IS NULL OR e.eventDate >= :rangeStart) " +
            " AND (cast(:rangeEnd as timestamp) IS NULL OR e.eventDate <= :rangeEnd) " +
            " ORDER BY e.eventDate DESC, e.id DESC")
    Page<Event> findByParamsForUnauthorizationUser(
            @Param("text") String text,
            @Param("categories") List<Long> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            PageRequest pageRequest);
}