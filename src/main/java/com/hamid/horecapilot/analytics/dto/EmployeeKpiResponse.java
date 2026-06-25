package com.hamid.horecapilot.analytics.dto;

import java.math.BigDecimal;

public record EmployeeKpiResponse(
    Long employeeId,
    String employeeNome,
    BigDecimal oreLavorate,
    BigDecimal laborCost
) {}
