package com.hamid.horecapilot.analytics.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PeriodSummaryResponse(
    LocalDate from,
    LocalDate to,
    BigDecimal fatturato,
    Integer coperti,
    BigDecimal laborCost,
    BigDecimal oreLavorate,
    BigDecimal laborCostPercent,
    BigDecimal scontrinoMedio,
    BigDecimal fatturatoPerOraLavoro
) {}
