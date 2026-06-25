package com.hamid.horecapilot.menu.dto;

import java.math.BigDecimal;

public record RecipeLineResponse(
    Long ingredientId,
    String ingredientNome,
    String unita,
    BigDecimal quantita,
    BigDecimal costoUnitario,
    BigDecimal costoRiga
) {}
