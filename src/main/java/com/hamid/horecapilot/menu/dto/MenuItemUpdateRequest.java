package com.hamid.horecapilot.menu.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record MenuItemUpdateRequest(
    @NotBlank @Size(max = 150) String nome,
    @NotNull @DecimalMin("0.0") @Digits(integer = 8, fraction = 2) BigDecimal prezzoVendita,
    @Size(max = 60) String categoria
) {}
