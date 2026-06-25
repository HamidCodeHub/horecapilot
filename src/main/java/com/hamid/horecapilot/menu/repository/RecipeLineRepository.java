package com.hamid.horecapilot.menu.repository;

import com.hamid.horecapilot.menu.model.RecipeLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeLineRepository extends JpaRepository<RecipeLine, Long> {

    List<RecipeLine> findByMenuItemId(Long menuItemId);

    Optional<RecipeLine> findByMenuItemIdAndIngredientId(Long menuItemId, Long ingredientId);
}
