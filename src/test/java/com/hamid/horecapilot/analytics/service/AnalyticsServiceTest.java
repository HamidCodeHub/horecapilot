package com.hamid.horecapilot.analytics.service;

import com.hamid.horecapilot.analytics.dto.DailyKpiResponse;
import com.hamid.horecapilot.analytics.dto.PeriodSummaryResponse;
import com.hamid.horecapilot.analytics.dto.StaffingFlag;
import com.hamid.horecapilot.common.BusinessRuleException;
import com.hamid.horecapilot.sales.dto.DailySalesResponse;
import com.hamid.horecapilot.sales.service.DailySalesService;
import com.hamid.horecapilot.staff.dto.ShiftResponse;
import com.hamid.horecapilot.staff.service.ShiftService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    ShiftService shiftService;
    @Mock
    DailySalesService dailySalesService;

    private AnalyticsService service;

    private static final LocalDate DAY1 = LocalDate.of(2024, 6, 10);
    private static final LocalDate DAY2 = LocalDate.of(2024, 6, 11);

    @BeforeEach
    void setUp() {
        KpiTargets targets = new KpiTargets(
            new BigDecimal("25"), new BigDecimal("33"), new BigDecimal("38"));
        service = new AnalyticsService(shiftService, dailySalesService, new KpiCalculator(), targets);
    }

    @Test
    void dailyBreakdown_aggregatesCorrectlyOverTwoDays() {
        when(shiftService.search(any(), any(), isNull())).thenReturn(List.of(
            shift(1L, DAY1, new BigDecimal("8.00"), new BigDecimal("100.00")),
            shift(2L, DAY1, new BigDecimal("6.00"), new BigDecimal("74.00")),
            shift(1L, DAY2, new BigDecimal("4.00"), new BigDecimal("50.00"))
        ));
        when(dailySalesService.search(any(), any())).thenReturn(List.of(
            sale(DAY1, new BigDecimal("1000.00"), 40),
            sale(DAY2, new BigDecimal("500.00"), 20)
        ));

        List<DailyKpiResponse> result = service.dailyBreakdown(DAY1, DAY2);

        assertThat(result).hasSize(2);

        DailyKpiResponse kpi1 = result.get(0);
        assertThat(kpi1.data()).isEqualTo(DAY1);
        assertThat(kpi1.laborCost()).isEqualByComparingTo(new BigDecimal("174.00")); // 100 + 74
        assertThat(kpi1.oreLavorate()).isEqualByComparingTo(new BigDecimal("14.00")); // 8 + 6
        assertThat(kpi1.fatturato()).isEqualByComparingTo(new BigDecimal("1000.00"));

        DailyKpiResponse kpi2 = result.get(1);
        assertThat(kpi2.data()).isEqualTo(DAY2);
        assertThat(kpi2.laborCost()).isEqualByComparingTo(new BigDecimal("50.00"));
    }

    @Test
    void dailyBreakdown_venditeConturniAssenti_laborCostZeroFlagUnknown() {
        when(shiftService.search(any(), any(), isNull())).thenReturn(List.of());
        when(dailySalesService.search(any(), any())).thenReturn(List.of(
            sale(DAY1, new BigDecimal("1000.00"), 40)
        ));

        List<DailyKpiResponse> result = service.dailyBreakdown(DAY1, DAY1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).laborCost()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.get(0).staffing()).isEqualTo(StaffingFlag.UNKNOWN);
    }

    @Test
    void periodSummary_aggregaTotali() {
        when(shiftService.search(any(), any(), isNull())).thenReturn(List.of(
            shift(1L, DAY1, new BigDecimal("8.00"), new BigDecimal("100.00")),
            shift(1L, DAY2, new BigDecimal("4.00"), new BigDecimal("50.00"))
        ));
        when(dailySalesService.search(any(), any())).thenReturn(List.of(
            sale(DAY1, new BigDecimal("1000.00"), 40),
            sale(DAY2, new BigDecimal("500.00"), 20)
        ));

        PeriodSummaryResponse result = service.periodSummary(DAY1, DAY2);

        assertThat(result.fatturato()).isEqualByComparingTo(new BigDecimal("1500.00"));
        assertThat(result.laborCost()).isEqualByComparingTo(new BigDecimal("150.00"));
        assertThat(result.coperti()).isEqualTo(60);
        // laborCostPercent = 150/1500*100 = 10.00
        assertThat(result.laborCostPercent()).isEqualByComparingTo(new BigDecimal("10.00"));
    }

    @Test
    void dailyBreakdown_fromAfterTo_throwsBusinessRuleException() {
        assertThatThrownBy(() -> service.dailyBreakdown(DAY2, DAY1))
            .isInstanceOf(BusinessRuleException.class);
    }

    private ShiftResponse shift(Long employeeId, LocalDate data,
                                 BigDecimal oreLavorate, BigDecimal costoTurno) {
        return new ShiftResponse(1L, employeeId, "Mario", data,
            LocalTime.of(9, 0), LocalTime.of(17, 0), "Chef", oreLavorate, costoTurno);
    }

    private DailySalesResponse sale(LocalDate data, BigDecimal fatturato, int coperti) {
        return new DailySalesResponse(1L, data, fatturato, coperti);
    }
}
