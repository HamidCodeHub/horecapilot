package com.hamid.horecapilot.menu.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record RecipeLineUpsertRequest(
    @NotNull @Positive BigDecimal quantita
) {}
