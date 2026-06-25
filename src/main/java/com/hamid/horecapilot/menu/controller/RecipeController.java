package com.hamid.horecapilot.menu.controller;

import com.hamid.horecapilot.menu.dto.RecipeLineUpsertRequest;
import com.hamid.horecapilot.menu.dto.RecipeResponse;
import com.hamid.horecapilot.menu.service.RecipeLineUpsertResult;
import com.hamid.horecapilot.menu.service.RecipeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/menu-items/{menuItemId}/recipe")
public class RecipeController {

    private final RecipeService service;

    public RecipeController(RecipeService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<RecipeResponse> getRecipe(@PathVariable Long menuItemId) {
        return ResponseEntity.ok(service.getRecipe(menuItemId));
    }

    @PutMapping("/{ingredientId}")
    public ResponseEntity<RecipeResponse> upsertLine(@PathVariable Long menuItemId,
                                                      @PathVariable Long ingredientId,
                                                      @Valid @RequestBody RecipeLineUpsertRequest request,
                                                      UriComponentsBuilder ucb) {
        RecipeLineUpsertResult result = service.upsertLine(menuItemId, ingredientId, request);
        if (result.created()) {
            URI location = ucb.path("/api/menu-items/{menuItemId}/recipe/{ingredientId}")
                .buildAndExpand(menuItemId, ingredientId).toUri();
            return ResponseEntity.created(location).body(result.recipe());
        }
        return ResponseEntity.ok(result.recipe());
    }

    @DeleteMapping("/{ingredientId}")
    public ResponseEntity<Void> removeLine(@PathVariable Long menuItemId,
                                            @PathVariable Long ingredientId) {
        service.removeLine(menuItemId, ingredientId);
        return ResponseEntity.noContent().build();
    }
}
