package com.hamid.horecapilot.menu.repository;

import com.hamid.horecapilot.menu.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
}
