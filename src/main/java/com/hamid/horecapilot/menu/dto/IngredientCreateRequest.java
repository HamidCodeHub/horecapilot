package com.hamid.horecapilot.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record IngredientCreateRequest(
    @NotBlank @Size(max = 150) @Schema(example = "Farina 00") String nome,
    @NotBlank @Size(max = 20) @Schema(example = "kg") String unita,
    @NotNull @DecimalMin("0.0") @Digits(integer = 8, fraction = 4) @Schema(example = "1.8000") BigDecimal costoUnitario
) {}
