package com.hamid.horecapilot.sales.repository;

import com.hamid.horecapilot.sales.model.DailySales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailySalesRepository extends JpaRepository<DailySales, Long> {

    Optional<DailySales> findByRestaurantIdAndData(Long restaurantId, LocalDate data);

    @Query("""
        SELECT d FROM DailySales d
        WHERE d.restaurantId = :restaurantId
          AND (CAST(:from AS LocalDate) IS NULL OR d.data >= :from)
          AND (CAST(:to   AS LocalDate) IS NULL OR d.data <= :to)
        ORDER BY d.data
        """)
    List<DailySales> search(@Param("restaurantId") Long restaurantId,
                            @Param("from") LocalDate from,
                            @Param("to") LocalDate to);
}
