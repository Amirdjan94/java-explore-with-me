package ru.practicum.ewm.admin.compilations.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.admin.compilations.model.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    @Query("SELECT c FROM Compilation c WHERE (:pinned IS NULL OR c.pinned = :pinned) " +
            " ORDER BY c.pinned DESC")
    Page<Compilation> findCompilationByPinned(@Param("pinned") Boolean pinned,
                                              PageRequest pageRequest);
}
