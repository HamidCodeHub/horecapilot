package com.hamid.horecapilot.menu.dto;

import java.math.BigDecimal;

public record MenuItemResponse(
    Long id,
    String nome,
    BigDecimal prezzoVendita,
    String categoria,
    boolean attivo
) {}
