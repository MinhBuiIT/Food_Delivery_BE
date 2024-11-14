package com.dev.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record CategoryIngredientWithListItem(
        List<IngredientItemFood> ingredientItems,
        String categoryIngredient,
        Boolean pick
) {
}
