package com.hamid.horecapilot.menu.controller;

import com.hamid.horecapilot.menu.dto.MenuItemCreateRequest;
import com.hamid.horecapilot.menu.dto.MenuItemResponse;
import com.hamid.horecapilot.menu.dto.MenuItemUpdateRequest;
import com.hamid.horecapilot.menu.service.MenuItemService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/menu-items")
public class MenuItemController {

    private final MenuItemService service;

    public MenuItemController(MenuItemService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<MenuItemResponse> create(@Valid @RequestBody MenuItemCreateRequest request,
                                                    UriComponentsBuilder ucb) {
        MenuItemResponse response = service.create(request);
        URI location = ucb.path("/api/menu-items/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItemResponse> getById(
        @PathVariable @Parameter(example = "1") Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<MenuItemResponse>> list() {
        return ResponseEntity.ok(service.list());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItemResponse> update(
        @PathVariable @Parameter(example = "1") Long id,
        @Valid @RequestBody MenuItemUpdateRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(
        @PathVariable @Parameter(example = "1") Long id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
