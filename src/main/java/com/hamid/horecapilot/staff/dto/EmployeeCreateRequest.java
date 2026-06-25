package com.hamid.horecapilot.staff.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record EmployeeCreateRequest(
    @NotBlank @Size(max = 150) @Schema(example = "Mario Rossi") String nome,
    @NotBlank @Size(max = 60) @Schema(example = "Chef de cuisine") String ruolo,
    @NotNull @DecimalMin("0.0") @Digits(integer = 6, fraction = 4) @Schema(example = "25.5000") BigDecimal costoOrarioAziendale
) {}
