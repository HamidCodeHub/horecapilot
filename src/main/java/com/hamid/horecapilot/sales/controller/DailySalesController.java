package com.hamid.horecapilot.sales.controller;

import com.hamid.horecapilot.sales.dto.DailySalesResponse;
import com.hamid.horecapilot.sales.dto.DailySalesUpsertRequest;
import com.hamid.horecapilot.sales.service.DailySalesService;
import com.hamid.horecapilot.sales.service.DailySalesUpsertResult;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/daily-sales")
public class DailySalesController {

    private final DailySalesService service;

    public DailySalesController(DailySalesService service) {
        this.service = service;
    }

    @PutMapping("/{data}")
    public ResponseEntity<DailySalesResponse> upsert(
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @Parameter(example = "2024-06-15") LocalDate data,
        @Valid @RequestBody DailySalesUpsertRequest request,
        UriComponentsBuilder ucb) {

        DailySalesUpsertResult result = service.upsert(data, request);
        if (result.created()) {
            URI location = ucb.path("/api/daily-sales/{data}").buildAndExpand(data).toUri();
            return ResponseEntity.created(location).body(result.sales());
        }
        return ResponseEntity.ok(result.sales());
    }

    @GetMapping("/{data}")
    public ResponseEntity<DailySalesResponse> getByDate(
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @Parameter(example = "2024-06-15") LocalDate data) {
        return ResponseEntity.ok(service.getByDate(data));
    }

    @GetMapping
    public ResponseEntity<List<DailySalesResponse>> search(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @Parameter(example = "2024-06-01") LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @Parameter(example = "2024-06-30") LocalDate to) {
        return ResponseEntity.ok(service.search(from, to));
    }

    @DeleteMapping("/{data}")
    public ResponseEntity<Void> delete(
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @Parameter(example = "2024-06-15") LocalDate data) {
        service.delete(data);
        return ResponseEntity.noContent().build();
    }
}
