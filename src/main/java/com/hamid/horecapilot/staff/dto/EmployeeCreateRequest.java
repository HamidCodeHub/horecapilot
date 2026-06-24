package com.hamid.horecapilot.staff.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record EmployeeCreateRequest(
    @NotBlank @Size(max = 150) String nome,
    @NotBlank @Size(max = 60) String ruolo,
    @NotNull @DecimalMin("0.0") @Digits(integer = 6, fraction = 4) BigDecimal costoOrarioAziendale
) {}
