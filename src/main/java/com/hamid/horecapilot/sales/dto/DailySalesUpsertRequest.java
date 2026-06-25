package com.hamid.horecapilot.sales.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DailySalesUpsertRequest(
    @NotNull @DecimalMin("0.0") @Digits(integer = 10, fraction = 2) @Schema(example = "1500.00") BigDecimal fatturato,
    @NotNull @Min(0) @Schema(example = "45") Integer coperti
) {}
