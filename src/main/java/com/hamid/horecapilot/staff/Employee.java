package com.hamid.horecapilot.staff;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "employee")
@Getter
@Setter
@NoArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "ruolo", nullable = false, length = 60)
    private String ruolo;

    @Column(name = "costo_orario_aziendale", nullable = false, precision = 10, scale = 4)
    private BigDecimal costoOrarioAziendale;

    @Column(name = "attivo", nullable = false)
    private boolean attivo;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    private void prePersist() {
        createdAt = OffsetDateTime.now();
    }
}
