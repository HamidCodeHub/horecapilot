package com.hamid.horecapilot.sales.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailySalesResponse(
    Long id,
    LocalDate data,
    BigDecimal fatturato,
    int coperti
) {}
