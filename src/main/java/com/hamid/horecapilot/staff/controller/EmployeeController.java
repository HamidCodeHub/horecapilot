package com.hamid.horecapilot.staff.controller;

import com.hamid.horecapilot.staff.dto.EmployeeCreateRequest;
import com.hamid.horecapilot.staff.dto.EmployeeResponse;
import com.hamid.horecapilot.staff.dto.EmployeeUpdateRequest;
import com.hamid.horecapilot.staff.service.EmployeeService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody EmployeeCreateRequest request,
                                                    UriComponentsBuilder ucb) {
        EmployeeResponse response = service.create(request);
        URI location = ucb.path("/api/employees/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getById(
        @PathVariable @Parameter(example = "1") Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> list() {
        return ResponseEntity.ok(service.list());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> update(
        @PathVariable @Parameter(example = "1") Long id,
        @Valid @RequestBody EmployeeUpdateRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(
        @PathVariable @Parameter(example = "1") Long id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
