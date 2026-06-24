package com.hamid.horecapilot.staff.dto;

import java.math.BigDecimal;

public record EmployeeResponse(
    Long id,
    String nome,
    String ruolo,
    BigDecimal costoOrarioAziendale,
    boolean attivo
) {}
