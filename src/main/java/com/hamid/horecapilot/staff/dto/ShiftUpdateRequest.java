package com.hamid.horecapilot.staff.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record ShiftUpdateRequest(
    @NotNull Long employeeId,
    @NotNull LocalDate data,
    @NotNull LocalTime oraInizio,
    @NotNull LocalTime oraFine,
    @NotBlank @Size(max = 60) String ruolo
) {}
