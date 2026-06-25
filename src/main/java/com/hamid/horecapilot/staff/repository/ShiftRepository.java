package com.hamid.horecapilot.staff.repository;

import com.hamid.horecapilot.staff.model.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift, Long> {

    @Query("""
        SELECT s FROM Shift s
        WHERE (CAST(:from AS LocalDate) IS NULL OR s.data >= :from)
          AND (CAST(:to AS LocalDate) IS NULL OR s.data <= :to)
          AND (CAST(:employeeId AS Long) IS NULL OR s.employee.id = :employeeId)
        ORDER BY s.data, s.oraInizio
        """)
    List<Shift> search(@Param("from") LocalDate from,
                       @Param("to") LocalDate to,
                       @Param("employeeId") Long employeeId);
}
