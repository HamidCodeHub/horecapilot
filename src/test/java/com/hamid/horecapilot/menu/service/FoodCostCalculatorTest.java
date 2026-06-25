package com.hamid.horecapilot.menu.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FoodCostCalculatorTest {

    private final FoodCostCalculator calculator = new FoodCostCalculator();

    @Test
    void calculate_quattrorighe_foodCostCorretto() {
        // (0.08×18.00)=1.44, (2×0.30)=0.60, (0.05×22.00)=1.10, (0.12×1.50)=0.18 → 3.32
        BigDecimal prezzoVendita = new BigDecimal("14.00");
        List<FoodCostCalculator.CalculationLine> lines = List.of(
            new FoodCostCalculator.CalculationLine(new BigDecimal("0.08"), new BigDecimal("18.00")),
            new FoodCostCalculator.CalculationLine(new BigDecimal("2"), new BigDecimal("0.30")),
            new FoodCostCalculator.CalculationLine(new BigDecimal("0.05"), new BigDecimal("22.00")),
            new FoodCostCalculator.CalculationLine(new BigDecimal("0.12"), new BigDecimal("1.50"))
        );

        FoodCostResult result = calculator.calculate(prezzoVendita, lines);

        assertThat(result.foodCost()).isEqualByComparingTo(new BigDecimal("3.32"));
        assertThat(result.foodCostPercent()).isEqualByComparingTo(new BigDecimal("23.71"));
        assertThat(result.margine()).isEqualByComparingTo(new BigDecimal("10.68"));
        assertThat(result.foodCostPercent()).isNotNull();
        assertThat(result.marginePercent()).isNotNull();
    }

    @Test
    void calculate_ricettaVuota_foodCostZero() {
        FoodCostResult result = calculator.calculate(new BigDecimal("14.00"), List.of());

        assertThat(result.foodCost()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.margine()).isEqualByComparingTo(new BigDecimal("14.00"));
    }

    @Test
    void calculate_prezzoVenditaZero_percentualiNull() {
        List<FoodCostCalculator.CalculationLine> lines = List.of(
            new FoodCostCalculator.CalculationLine(new BigDecimal("1"), new BigDecimal("2.00"))
        );

        FoodCostResult result = calculator.calculate(BigDecimal.ZERO, lines);

        assertThat(result.foodCostPercent()).isNull();
        assertThat(result.marginePercent()).isNull();
        assertThat(result.foodCost()).isEqualByComparingTo(new BigDecimal("2.00"));
    }
}
