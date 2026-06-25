package com.hamid.horecapilot.menu.service;

import com.hamid.horecapilot.common.BusinessRuleException;
import com.hamid.horecapilot.common.EntityNotFoundException;
import com.hamid.horecapilot.menu.dto.RecipeLineUpsertRequest;
import com.hamid.horecapilot.menu.model.Ingredient;
import com.hamid.horecapilot.menu.model.MenuItem;
import com.hamid.horecapilot.menu.model.RecipeLine;
import com.hamid.horecapilot.menu.repository.IngredientRepository;
import com.hamid.horecapilot.menu.repository.MenuItemRepository;
import com.hamid.horecapilot.menu.repository.RecipeLineRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    RecipeLineRepository recipeLineRepository;
    @Mock
    MenuItemRepository menuItemRepository;
    @Mock
    IngredientRepository ingredientRepository;
    @Mock
    FoodCostCalculator foodCostCalculator;

    @InjectMocks
    RecipeService service;

    @Test
    void upsertLine_crea_quandoRigaNonEsiste() {
        MenuItem menuItem = menuItem(1L, true);
        Ingredient ingredient = ingredient(2L, true);

        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(menuItem));
        when(ingredientRepository.findById(2L)).thenReturn(Optional.of(ingredient));
        when(recipeLineRepository.findByMenuItemIdAndIngredientId(1L, 2L)).thenReturn(Optional.empty());
        when(recipeLineRepository.save(any())).thenReturn(recipeLine(menuItem, ingredient, "0.08"));
        when(recipeLineRepository.findByMenuItemId(1L)).thenReturn(List.of());
        when(foodCostCalculator.calculate(any(), any()))
            .thenReturn(new FoodCostResult(BigDecimal.ZERO, null, new BigDecimal("14.00"), null));

        RecipeLineUpsertResult result = service.upsertLine(1L, 2L,
            new RecipeLineUpsertRequest(new BigDecimal("0.08")));

        assertThat(result.created()).isTrue();
    }

    @Test
    void upsertLine_aggiorna_quandoRigaEsiste() {
        MenuItem menuItem = menuItem(1L, true);
        Ingredient ingredient = ingredient(2L, true);
        RecipeLine existing = recipeLine(menuItem, ingredient, "0.08");

        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(menuItem));
        when(ingredientRepository.findById(2L)).thenReturn(Optional.of(ingredient));
        when(recipeLineRepository.findByMenuItemIdAndIngredientId(1L, 2L)).thenReturn(Optional.of(existing));
        when(recipeLineRepository.findByMenuItemId(1L)).thenReturn(List.of(existing));
        when(foodCostCalculator.calculate(any(), any()))
            .thenReturn(new FoodCostResult(new BigDecimal("2.00"), new BigDecimal("14.29"),
                new BigDecimal("12.00"), new BigDecimal("85.71")));

        RecipeLineUpsertResult result = service.upsertLine(1L, 2L,
            new RecipeLineUpsertRequest(new BigDecimal("0.15")));

        assertThat(result.created()).isFalse();
        assertThat(existing.getQuantita()).isEqualByComparingTo(new BigDecimal("0.15"));
    }

    @Test
    void upsertLine_throwsEntityNotFoundException_quandoIngredienteNonEsiste() {
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(menuItem(1L, true)));
        when(ingredientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.upsertLine(1L, 99L,
                new RecipeLineUpsertRequest(new BigDecimal("0.08"))))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("99");
    }

    @Test
    void upsertLine_throwsBusinessRuleException_quandoIngredienteNonAttivo() {
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(menuItem(1L, true)));
        when(ingredientRepository.findById(2L)).thenReturn(Optional.of(ingredient(2L, false)));
        when(recipeLineRepository.findByMenuItemIdAndIngredientId(1L, 2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.upsertLine(1L, 2L,
                new RecipeLineUpsertRequest(new BigDecimal("0.08"))))
            .isInstanceOf(BusinessRuleException.class);
    }

    private MenuItem menuItem(Long id, boolean attivo) {
        MenuItem m = new MenuItem();
        m.setId(id);
        m.setRestaurantId(1L);
        m.setNome("Pasta");
        m.setPrezzoVendita(new BigDecimal("14.00"));
        m.setAttivo(attivo);
        return m;
    }

    private Ingredient ingredient(Long id, boolean attivo) {
        Ingredient i = new Ingredient();
        i.setId(id);
        i.setRestaurantId(1L);
        i.setNome("Farina");
        i.setUnita("kg");
        i.setCostoUnitario(new BigDecimal("18.00"));
        i.setAttivo(attivo);
        return i;
    }

    private RecipeLine recipeLine(MenuItem menuItem, Ingredient ingredient, String quantita) {
        RecipeLine line = new RecipeLine();
        line.setMenuItem(menuItem);
        line.setIngredient(ingredient);
        line.setQuantita(new BigDecimal(quantita));
        return line;
    }
}
