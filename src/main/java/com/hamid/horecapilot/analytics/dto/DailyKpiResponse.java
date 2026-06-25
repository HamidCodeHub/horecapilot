package com.hamid.horecapilot.analytics.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyKpiResponse(
    LocalDate data,
    BigDecimal fatturato,
    Integer coperti,
    BigDecimal laborCost,
    BigDecimal oreLavorate,
    BigDecimal laborCostPercent,
    BigDecimal scontrinoMedio,
    BigDecimal fatturatoPerOraLavoro,
    StaffingFlag staffing
) {}
