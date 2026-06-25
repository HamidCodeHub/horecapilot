package com.hamid.horecapilot.analytics.service;

import com.hamid.horecapilot.analytics.dto.DailyKpiResponse;
import com.hamid.horecapilot.analytics.dto.EmployeeKpiResponse;
import com.hamid.horecapilot.analytics.dto.PeriodSummaryResponse;
import com.hamid.horecapilot.common.BusinessRuleException;
import com.hamid.horecapilot.sales.dto.DailySalesResponse;
import com.hamid.horecapilot.sales.service.DailySalesService;
import com.hamid.horecapilot.staff.dto.ShiftResponse;
import com.hamid.horecapilot.staff.service.ShiftService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final ShiftService shiftService;
    private final DailySalesService dailySalesService;
    private final KpiCalculator kpiCalculator;
    private final KpiTargets targets;

    public AnalyticsService(ShiftService shiftService,
                             DailySalesService dailySalesService,
                             KpiCalculator kpiCalculator,
                             KpiTargets targets) {
        this.shiftService = shiftService;
        this.dailySalesService = dailySalesService;
        this.kpiCalculator = kpiCalculator;
        this.targets = targets;
    }

    @Transactional(readOnly = true)
    public List<DailyKpiResponse> dailyBreakdown(LocalDate from, LocalDate to) {
        validateRange(from, to);

        Map<LocalDate, List<ShiftResponse>> shiftsByDate = shiftService.search(from, to, null)
            .stream().collect(Collectors.groupingBy(ShiftResponse::data));

        Map<LocalDate, DailySalesResponse> salesByDate = dailySalesService.search(from, to)
            .stream().collect(Collectors.toMap(DailySalesResponse::data, s -> s));

        TreeSet<LocalDate> allDates = new TreeSet<>();
        allDates.addAll(shiftsByDate.keySet());
        allDates.addAll(salesByDate.keySet());

        return allDates.stream()
            .map(date -> buildDailyKpi(date,
                shiftsByDate.getOrDefault(date, List.of()),
                salesByDate.get(date)))
            .toList();
    }

    @Transactional(readOnly = true)
    public PeriodSummaryResponse periodSummary(LocalDate from, LocalDate to) {
        validateRange(from, to);

        List<ShiftResponse> shifts = shiftService.search(from, to, null);
        List<DailySalesResponse> sales = dailySalesService.search(from, to);

        BigDecimal totalFatturato = sales.stream()
            .map(DailySalesResponse::fatturato)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        int totalCoperti = sales.stream().mapToInt(DailySalesResponse::coperti).sum();
        BigDecimal totalLaborCost = shifts.stream()
            .map(ShiftResponse::costoTurno)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalOreLavorate = shifts.stream()
            .map(ShiftResponse::oreLavorate)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new PeriodSummaryResponse(
            from, to,
            totalFatturato,
            totalCoperti,
            totalLaborCost,
            totalOreLavorate,
            kpiCalculator.laborCostPercent(totalLaborCost, totalFatturato),
            kpiCalculator.scontrinoMedio(totalFatturato, totalCoperti),
            kpiCalculator.fatturatoPerOraLavoro(totalFatturato, totalOreLavorate)
        );
    }

    @Transactional(readOnly = true)
    public List<EmployeeKpiResponse> byEmployee(LocalDate from, LocalDate to) {
        validateRange(from, to);

        return shiftService.search(from, to, null).stream()
            .collect(Collectors.groupingBy(ShiftResponse::employeeId))
            .entrySet().stream()
            .map(entry -> {
                List<ShiftResponse> empShifts = entry.getValue();
                BigDecimal ore = empShifts.stream()
                    .map(ShiftResponse::oreLavorate)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal costo = empShifts.stream()
                    .map(ShiftResponse::costoTurno)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                return new EmployeeKpiResponse(entry.getKey(), empShifts.get(0).employeeNome(), ore, costo);
            })
            .sorted(Comparator.comparing(EmployeeKpiResponse::laborCost).reversed())
            .toList();
    }

    private DailyKpiResponse buildDailyKpi(LocalDate date,
                                            List<ShiftResponse> dayShifts,
                                            DailySalesResponse daySales) {
        BigDecimal laborCost = dayShifts.stream()
            .map(ShiftResponse::costoTurno)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal oreLavorate = dayShifts.stream()
            .map(ShiftResponse::oreLavorate)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal fatturato = daySales != null ? daySales.fatturato() : null;
        Integer coperti = daySales != null ? daySales.coperti() : null;

        BigDecimal laborPct = kpiCalculator.laborCostPercent(laborCost, fatturato);
        BigDecimal scontrino = kpiCalculator.scontrinoMedio(fatturato, coperti);
        BigDecimal splh = kpiCalculator.fatturatoPerOraLavoro(fatturato, oreLavorate);

        return new DailyKpiResponse(
            date, fatturato, coperti, laborCost, oreLavorate,
            laborPct, scontrino, splh,
            kpiCalculator.staffingFlag(laborPct, oreLavorate, targets)
        );
    }

    private void validateRange(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new BusinessRuleException("'from' must not be after 'to': " + from + " > " + to);
        }
    }
}
