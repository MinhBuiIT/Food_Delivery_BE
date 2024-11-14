package com.dev.dto.response;

public record IngredientItemFood(
        Long id,
        String name,
        Long price,
        boolean stock
) {
    
}
