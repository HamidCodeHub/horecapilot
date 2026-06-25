package com.hamid.horecapilot.menu.service;

import com.hamid.horecapilot.menu.dto.RecipeResponse;

public record RecipeLineUpsertResult(boolean created, RecipeResponse recipe) {}
