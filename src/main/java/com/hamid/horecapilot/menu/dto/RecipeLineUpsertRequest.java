package com.hamid.horecapilot.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record RecipeLineUpsertRequest(
    @NotNull @Positive @Schema(example = "0.08") BigDecimal quantita
) {}
