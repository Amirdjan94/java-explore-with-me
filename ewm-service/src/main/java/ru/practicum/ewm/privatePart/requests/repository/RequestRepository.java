package ru.practicum.ewm.privatePart.requests.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.privatePart.requests.data.RequestStatus;
import ru.practicum.ewm.privatePart.requests.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("SELECT COUNT(r) FROM Request r WHERE r.event.id = :eventId " +
            "AND r.requestStatus = :status")
    Long getRequestCountByEventIdAndStatus(@Param("eventId") Long eventId,
                                           @Param("status") RequestStatus status);

    @Query("SELECT r FROM Request r WHERE r.user.id = :userId ")
    List<Request> getRequestListByUserId(@Param("userId") Long userId);

    @Query("SELECT r FROM Request r WHERE r.event.id IN :ids " +
            "AND r.requestStatus = :status")
    List<Request> findAllRequestByEventIds(@Param("ids") Iterable<Long> ids,
                                           @Param("status") RequestStatus status);

    @Query("SELECT r FROM Request r WHERE r.event.id = :id " +
            "ORDER BY r.created")
    List<Request> findAllRequestByEventId(@Param("id") Long id);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Request r SET r.requestStatus = :status WHERE r.id IN :ids")
    void updateUsersStatus(@Param("status") RequestStatus status, @Param("ids") List<Long> ids);

    @Query("SELECT r FROM Request r WHERE r.user.id = :userId " +
            "AND r.event.id = :eventId")
    Optional<Request> getRequestByUserIdAndEventId(@Param("userId") Long userId,
                                                   @Param("eventId") Long eventId);
}