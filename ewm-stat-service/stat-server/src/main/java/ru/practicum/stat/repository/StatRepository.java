package ru.practicum.stat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.stat.model.Stat;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<Stat, Long> {

    @Query("select s.app, s.uri, count(s) from Stat s " +
            "where s.timestamp >= :start and s.timestamp <= :end " +
            "and (:uris is null or s.uri in :uris) " +
            "group by s.app, s.uri " +
            "order by count(s) desc")
    List<Object[]> getAllStatsByParam(@Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end,
                                      @Param("uris") List<String> uris);

    @Query("select s.app, s.uri, count(distinct s.ip) from Stat s " +
            "where s.timestamp >= :start and s.timestamp <= :end " +
            "and (:uris is null or s.uri in :uris) " +
            "group by s.app, s.uri " +
            "order by count(distinct s.ip) desc")
    List<Object[]> getAllStatsByParamAndUnique(@Param("start") LocalDateTime start,
                                               @Param("end") LocalDateTime end,
                                               @Param("uris") List<String> uris);
}