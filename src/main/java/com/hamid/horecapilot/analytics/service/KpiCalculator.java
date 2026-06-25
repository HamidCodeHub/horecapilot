package com.hamid.horecapilot.analytics.service;

import com.hamid.horecapilot.analytics.dto.StaffingFlag;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class KpiCalculator {

    public BigDecimal laborCostPercent(BigDecimal laborCost, BigDecimal fatturato) {
        if (fatturato == null || fatturato.compareTo(BigDecimal.ZERO) == 0) return null;
        return laborCost.multiply(BigDecimal.valueOf(100))
            .divide(fatturato, 2, RoundingMode.HALF_UP);
    }

    public BigDecimal scontrinoMedio(BigDecimal fatturato, Integer coperti) {
        if (fatturato == null || coperti == null || coperti == 0) return null;
        return fatturato.divide(BigDecimal.valueOf(coperti), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal fatturatoPerOraLavoro(BigDecimal fatturato, BigDecimal oreLavorate) {
        if (fatturato == null || oreLavorate == null || oreLavorate.compareTo(BigDecimal.ZERO) == 0) return null;
        return fatturato.divide(oreLavorate, 2, RoundingMode.HALF_UP);
    }

    public StaffingFlag staffingFlag(BigDecimal laborCostPercent, BigDecimal oreLavorate, KpiTargets targets) {
        if (laborCostPercent == null || oreLavorate == null || oreLavorate.compareTo(BigDecimal.ZERO) == 0) {
            return StaffingFlag.UNKNOWN;
        }
        if (laborCostPercent.compareTo(targets.laborCostPercentUpper()) > 0) return StaffingFlag.OVERSTAFFED;
        if (laborCostPercent.compareTo(targets.laborCostPercentLower()) < 0) return StaffingFlag.UNDERSTAFFED;
        return StaffingFlag.OK;
    }
}
