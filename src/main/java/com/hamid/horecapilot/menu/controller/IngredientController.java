package com.hamid.horecapilot.menu.controller;

import com.hamid.horecapilot.menu.dto.IngredientCreateRequest;
import com.hamid.horecapilot.menu.dto.IngredientResponse;
import com.hamid.horecapilot.menu.dto.IngredientUpdateRequest;
import com.hamid.horecapilot.menu.service.IngredientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {

    private final IngredientService service;

    public IngredientController(IngredientService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<IngredientResponse> create(@Valid @RequestBody IngredientCreateRequest request,
                                                      UriComponentsBuilder ucb) {
        IngredientResponse response = service.create(request);
        URI location = ucb.path("/api/ingredients/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredientResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<IngredientResponse>> list() {
        return ResponseEntity.ok(service.list());
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngredientResponse> update(@PathVariable Long id,
                                                      @Valid @RequestBody IngredientUpdateRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
