package com.hamid.horecapilot.analytics.controller;

import com.hamid.horecapilot.analytics.dto.DailyKpiResponse;
import com.hamid.horecapilot.analytics.dto.EmployeeKpiResponse;
import com.hamid.horecapilot.analytics.dto.PeriodSummaryResponse;
import com.hamid.horecapilot.analytics.service.AnalyticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService service;

    public AnalyticsController(AnalyticsService service) {
        this.service = service;
    }

    @GetMapping("/summary")
    public ResponseEntity<PeriodSummaryResponse> summary(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(service.periodSummary(from, to));
    }

    @GetMapping("/daily")
    public ResponseEntity<List<DailyKpiResponse>> daily(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(service.dailyBreakdown(from, to));
    }

    @GetMapping("/by-employee")
    public ResponseEntity<List<EmployeeKpiResponse>> byEmployee(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(service.byEmployee(from, to));
    }
}
