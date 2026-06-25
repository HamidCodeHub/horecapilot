package com.hamid.horecapilot.menu.service;

import com.hamid.horecapilot.common.EntityNotFoundException;
import com.hamid.horecapilot.common.Tenant;
import com.hamid.horecapilot.menu.dto.IngredientCreateRequest;
import com.hamid.horecapilot.menu.dto.IngredientResponse;
import com.hamid.horecapilot.menu.dto.IngredientUpdateRequest;
import com.hamid.horecapilot.menu.model.Ingredient;
import com.hamid.horecapilot.menu.repository.IngredientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IngredientService {

    private final IngredientRepository repository;

    public IngredientService(IngredientRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public IngredientResponse create(IngredientCreateRequest req) {
        Ingredient ingredient = new Ingredient();
        ingredient.setRestaurantId(Tenant.DEFAULT_RESTAURANT_ID);
        ingredient.setNome(req.nome());
        ingredient.setUnita(req.unita());
        ingredient.setCostoUnitario(req.costoUnitario());
        ingredient.setAttivo(true);
        return toResponse(repository.save(ingredient));
    }

    @Transactional
    public IngredientResponse update(Long id, IngredientUpdateRequest req) {
        Ingredient ingredient = findOrThrow(id);
        ingredient.setNome(req.nome());
        ingredient.setUnita(req.unita());
        ingredient.setCostoUnitario(req.costoUnitario());
        return toResponse(ingredient);
    }

    public IngredientResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public List<IngredientResponse> list() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public void deactivate(Long id) {
        findOrThrow(id).setAttivo(false);
    }

    private Ingredient findOrThrow(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Ingredient not found with id: " + id));
    }

    private IngredientResponse toResponse(Ingredient i) {
        return new IngredientResponse(i.getId(), i.getNome(), i.getUnita(), i.getCostoUnitario(), i.isAttivo());
    }
}
