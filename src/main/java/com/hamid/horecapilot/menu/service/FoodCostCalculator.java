package com.hamid.horecapilot.menu.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class FoodCostCalculator {

    public record CalculationLine(BigDecimal quantita, BigDecimal costoUnitario) {}

    public FoodCostResult calculate(BigDecimal prezzoVendita, List<CalculationLine> lines) {
        BigDecimal foodCost = lines.stream()
            .map(l -> l.quantita().multiply(l.costoUnitario()))
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.HALF_UP);

        BigDecimal margine = prezzoVendita.subtract(foodCost).setScale(2, RoundingMode.HALF_UP);

        BigDecimal foodCostPercent = null;
        BigDecimal marginePercent = null;

        if (prezzoVendita.compareTo(BigDecimal.ZERO) > 0) {
            foodCostPercent = foodCost.multiply(BigDecimal.valueOf(100))
                .divide(prezzoVendita, 2, RoundingMode.HALF_UP);
            marginePercent = margine.multiply(BigDecimal.valueOf(100))
                .divide(prezzoVendita, 2, RoundingMode.HALF_UP);
        }

        return new FoodCostResult(foodCost, foodCostPercent, margine, marginePercent);
    }
}
