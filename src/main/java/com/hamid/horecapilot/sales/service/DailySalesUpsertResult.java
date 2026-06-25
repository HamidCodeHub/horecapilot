package com.hamid.horecapilot.sales.service;

import com.hamid.horecapilot.sales.dto.DailySalesResponse;

public record DailySalesUpsertResult(boolean created, DailySalesResponse sales) {}
