package com.hamid.horecapilot.staff.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record ShiftUpdateRequest(
    @NotNull @Schema(example = "1") Long employeeId,
    @NotNull @Schema(example = "2024-06-15") LocalDate data,
    @NotNull @Schema(example = "17:00") LocalTime oraInizio,
    @NotNull @Schema(example = "23:00") LocalTime oraFine,
    @NotBlank @Size(max = 60) @Schema(example = "Chef") String ruolo
) {}
