package com.dev.dto.response;

import com.dev.models.Food;

import java.util.Set;

public record CategoryIngredientResponse(
        Long id,
        String name,
        Boolean pick
) {
}
