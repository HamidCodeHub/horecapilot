package com.hamid.horecapilot.menu.dto;

import java.math.BigDecimal;
import java.util.List;

public record RecipeResponse(
    Long menuItemId,
    BigDecimal prezzoVendita,
    List<RecipeLineResponse> lines,
    BigDecimal foodCost,
    BigDecimal foodCostPercent,
    BigDecimal margine,
    BigDecimal marginePercent
) {}
