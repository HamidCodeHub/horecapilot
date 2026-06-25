package com.hamid.horecapilot.menu.service;

import java.math.BigDecimal;

public record FoodCostResult(
    BigDecimal foodCost,
    BigDecimal foodCostPercent,
    BigDecimal margine,
    BigDecimal marginePercent
) {}
