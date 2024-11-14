package com.dev.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateQuantityCartItemRequest(
        @NotNull(message = "Food Id must be required")
        Long cartItemId,
        @Positive(message = "Quantity must be positive")
        Integer quantity
) {
}
