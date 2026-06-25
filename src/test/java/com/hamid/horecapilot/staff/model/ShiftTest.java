package com.hamid.horecapilot.staff.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class ShiftTest {

    @Test
    void oreLavorate_turnoNormale() {
        Shift shift = shift(LocalTime.of(17, 0), LocalTime.of(23, 0));
        assertThat(shift.oreLavorate()).isEqualByComparingTo(new BigDecimal("6.0000"));
    }

    @Test
    void oreLavorate_scavalcaMezzanotte() {
        Shift shift = shift(LocalTime.of(20, 0), LocalTime.of(2, 0));
        assertThat(shift.oreLavorate()).isEqualByComparingTo(new BigDecimal("6.0000"));
    }

    @Test
    void oreLavorate_conMinuti() {
        Shift shift = shift(LocalTime.of(18, 30), LocalTime.of(22, 15));
        assertThat(shift.oreLavorate()).isEqualByComparingTo(new BigDecimal("3.7500"));
    }

    private Shift shift(LocalTime oraInizio, LocalTime oraFine) {
        Shift s = new Shift();
        s.setData(LocalDate.of(2024, 1, 15));
        s.setOraInizio(oraInizio);
        s.setOraFine(oraFine);
        return s;
    }
}
