package com.hamid.horecapilot.menu.dto;

import java.math.BigDecimal;

public record IngredientResponse(
    Long id,
    String nome,
    String unita,
    BigDecimal costoUnitario,
    boolean attivo
) {}
