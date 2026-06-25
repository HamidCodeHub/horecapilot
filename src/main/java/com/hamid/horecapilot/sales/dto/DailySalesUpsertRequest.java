package com.hamid.horecapilot.sales.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DailySalesUpsertRequest(
    @NotNull @DecimalMin("0.0") @Digits(integer = 10, fraction = 2) BigDecimal fatturato,
    @NotNull @Min(0) Integer coperti
) {}
