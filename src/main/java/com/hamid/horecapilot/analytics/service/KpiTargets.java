package com.hamid.horecapilot.analytics.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "horecapilot.targets")
public record KpiTargets(
    BigDecimal laborCostPercentLower,
    BigDecimal laborCostPercentTarget,
    BigDecimal laborCostPercentUpper
) {}
