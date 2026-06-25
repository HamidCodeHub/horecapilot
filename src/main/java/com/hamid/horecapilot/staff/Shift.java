package com.hamid.horecapilot.staff;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "shift")
@Getter
@Setter
@NoArgsConstructor
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "data", nullable = false)
    private LocalDate data;

    @Column(name = "ora_inizio", nullable = false)
    private LocalTime oraInizio;

    @Column(name = "ora_fine", nullable = false)
    private LocalTime oraFine;

    @Column(name = "ruolo", nullable = false, length = 60)
    private String ruolo;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    private void prePersist() {
        createdAt = OffsetDateTime.now();
    }

    public BigDecimal oreLavorate() {
        LocalDateTime inizio = LocalDateTime.of(data, oraInizio);
        LocalDateTime fine = oraFine.isAfter(oraInizio)
            ? LocalDateTime.of(data, oraFine)
            : LocalDateTime.of(data.plusDays(1), oraFine);
        long secondi = Duration.between(inizio, fine).toSeconds();
        return BigDecimal.valueOf(secondi).divide(BigDecimal.valueOf(3600), 4, RoundingMode.HALF_UP);
    }
}
