package com.hamid.horecapilot.staff.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record ShiftResponse(
    Long id,
    Long employeeId,
    String employeeNome,
    LocalDate data,
    LocalTime oraInizio,
    LocalTime oraFine,
    String ruolo,
    BigDecimal oreLavorate,
    BigDecimal costoTurno
) {}
