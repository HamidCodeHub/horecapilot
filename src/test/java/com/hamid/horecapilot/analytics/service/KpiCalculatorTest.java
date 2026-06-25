package com.hamid.horecapilot.analytics.service;

import com.hamid.horecapilot.analytics.dto.StaffingFlag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class KpiCalculatorTest {

    private final KpiCalculator calc = new KpiCalculator();
    private final KpiTargets targets = new KpiTargets(
        new BigDecimal("25"), new BigDecimal("33"), new BigDecimal("38"));

    // laborCostPercent

    @Test
    void laborCostPercent_normal() {
        assertThat(calc.laborCostPercent(new BigDecimal("330"), new BigDecimal("1000")))
            .isEqualByComparingTo(new BigDecimal("33.00"));
    }

    @Test
    void laborCostPercent_fatturatoZero_returnsNull() {
        assertThat(calc.laborCostPercent(new BigDecimal("330"), BigDecimal.ZERO)).isNull();
    }

    @Test
    void laborCostPercent_fatturatoNull_returnsNull() {
        assertThat(calc.laborCostPercent(new BigDecimal("330"), null)).isNull();
    }

    // scontrinoMedio

    @Test
    void scontrinoMedio_normal() {
        assertThat(calc.scontrinoMedio(new BigDecimal("1000"), 40))
            .isEqualByComparingTo(new BigDecimal("25.00"));
    }

    @Test
    void scontrinoMedio_coperitiZero_returnsNull() {
        assertThat(calc.scontrinoMedio(new BigDecimal("1000"), 0)).isNull();
    }

    // fatturatoPerOraLavoro

    @Test
    void fatturatoPerOraLavoro_normal() {
        assertThat(calc.fatturatoPerOraLavoro(new BigDecimal("1000"), new BigDecimal("80")))
            .isEqualByComparingTo(new BigDecimal("12.50"));
    }

    @Test
    void fatturatoPerOraLavoro_oreZero_returnsNull() {
        assertThat(calc.fatturatoPerOraLavoro(new BigDecimal("1000"), BigDecimal.ZERO)).isNull();
    }

    // staffingFlag

    @Test
    void staffingFlag_overstaffed() {
        assertThat(calc.staffingFlag(new BigDecimal("42"), new BigDecimal("8"), targets))
            .isEqualTo(StaffingFlag.OVERSTAFFED);
    }

    @Test
    void staffingFlag_understaffed() {
        assertThat(calc.staffingFlag(new BigDecimal("20"), new BigDecimal("8"), targets))
            .isEqualTo(StaffingFlag.UNDERSTAFFED);
    }

    @Test
    void staffingFlag_ok() {
        assertThat(calc.staffingFlag(new BigDecimal("33"), new BigDecimal("8"), targets))
            .isEqualTo(StaffingFlag.OK);
    }

    @Test
    void staffingFlag_laborCostPercentNull_unknown() {
        assertThat(calc.staffingFlag(null, new BigDecimal("8"), targets))
            .isEqualTo(StaffingFlag.UNKNOWN);
    }

    @Test
    void staffingFlag_oreZero_unknown() {
        assertThat(calc.staffingFlag(new BigDecimal("33"), BigDecimal.ZERO, targets))
            .isEqualTo(StaffingFlag.UNKNOWN);
    }
}
