package com.dev.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record AddCartItemRequest(
        @Positive(message = "Quantity must be positive")
        Integer quantity,

        @NotNull(message = "Food Id must be required")
        Long foodId,
        String specialInstructions,
        List<Long> ingredientIds
) {
}
