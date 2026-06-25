package com.hamid.horecapilot.menu.service;

import com.hamid.horecapilot.common.BusinessRuleException;
import com.hamid.horecapilot.common.EntityNotFoundException;
import com.hamid.horecapilot.menu.dto.RecipeLineResponse;
import com.hamid.horecapilot.menu.dto.RecipeLineUpsertRequest;
import com.hamid.horecapilot.menu.dto.RecipeResponse;
import com.hamid.horecapilot.menu.model.Ingredient;
import com.hamid.horecapilot.menu.model.MenuItem;
import com.hamid.horecapilot.menu.model.RecipeLine;
import com.hamid.horecapilot.menu.repository.IngredientRepository;
import com.hamid.horecapilot.menu.repository.MenuItemRepository;
import com.hamid.horecapilot.menu.repository.RecipeLineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class RecipeService {

    private final RecipeLineRepository recipeLineRepository;
    private final MenuItemRepository menuItemRepository;
    private final IngredientRepository ingredientRepository;
    private final FoodCostCalculator foodCostCalculator;

    public RecipeService(RecipeLineRepository recipeLineRepository,
                         MenuItemRepository menuItemRepository,
                         IngredientRepository ingredientRepository,
                         FoodCostCalculator foodCostCalculator) {
        this.recipeLineRepository = recipeLineRepository;
        this.menuItemRepository = menuItemRepository;
        this.ingredientRepository = ingredientRepository;
        this.foodCostCalculator = foodCostCalculator;
    }

    @Transactional
    public RecipeLineUpsertResult upsertLine(Long menuItemId, Long ingredientId, RecipeLineUpsertRequest req) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
            .orElseThrow(() -> new EntityNotFoundException("MenuItem not found with id: " + menuItemId));
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
            .orElseThrow(() -> new EntityNotFoundException("Ingredient not found with id: " + ingredientId));

        boolean created = recipeLineRepository.findByMenuItemIdAndIngredientId(menuItemId, ingredientId)
            .map(existing -> {
                existing.setQuantita(req.quantita());
                return false;
            })
            .orElseGet(() -> {
                if (!menuItem.isAttivo()) {
                    throw new BusinessRuleException("Cannot add recipe line to inactive menu item: " + menuItemId);
                }
                if (!ingredient.isAttivo()) {
                    throw new BusinessRuleException("Cannot add inactive ingredient to recipe: " + ingredientId);
                }
                RecipeLine line = new RecipeLine();
                line.setMenuItem(menuItem);
                line.setIngredient(ingredient);
                line.setQuantita(req.quantita());
                recipeLineRepository.save(line);
                return true;
            });

        return new RecipeLineUpsertResult(created, buildRecipeResponse(menuItem));
    }

    @Transactional
    public void removeLine(Long menuItemId, Long ingredientId) {
        RecipeLine line = recipeLineRepository.findByMenuItemIdAndIngredientId(menuItemId, ingredientId)
            .orElseThrow(() -> new EntityNotFoundException(
                "Recipe line not found for menuItem " + menuItemId + " and ingredient " + ingredientId));
        recipeLineRepository.delete(line);
    }

    @Transactional(readOnly = true)
    public RecipeResponse getRecipe(Long menuItemId) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
            .orElseThrow(() -> new EntityNotFoundException("MenuItem not found with id: " + menuItemId));
        return buildRecipeResponse(menuItem);
    }

    private RecipeResponse buildRecipeResponse(MenuItem menuItem) {
        List<RecipeLine> lines = recipeLineRepository.findByMenuItemId(menuItem.getId());

        List<RecipeLineResponse> lineResponses = lines.stream()
            .map(this::toLineResponse)
            .toList();

        List<FoodCostCalculator.CalculationLine> calcLines = lines.stream()
            .map(l -> new FoodCostCalculator.CalculationLine(
                l.getQuantita(), l.getIngredient().getCostoUnitario()))
            .toList();

        FoodCostResult result = foodCostCalculator.calculate(menuItem.getPrezzoVendita(), calcLines);

        return new RecipeResponse(
            menuItem.getId(),
            menuItem.getPrezzoVendita(),
            lineResponses,
            result.foodCost(),
            result.foodCostPercent(),
            result.margine(),
            result.marginePercent()
        );
    }

    private RecipeLineResponse toLineResponse(RecipeLine line) {
        BigDecimal costoRiga = line.getQuantita()
            .multiply(line.getIngredient().getCostoUnitario())
            .setScale(2, RoundingMode.HALF_UP);
        return new RecipeLineResponse(
            line.getIngredient().getId(),
            line.getIngredient().getNome(),
            line.getIngredient().getUnita(),
            line.getQuantita(),
            line.getIngredient().getCostoUnitario(),
            costoRiga
        );
    }
}
