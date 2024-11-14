package com.dev.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record IngredientItemRequest(
        @NotBlank(message = "Name ingredient is required")
        String name,

        @Positive(message = "Price is a positive number")
        Long price,

        @NotBlank(message = "Category ingredient is required")
        String categoryIngredient
) {
}
