package com.dev.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryIngredientRequest(
        @NotBlank(message = "Category ingredient's name must not be empty")
        String name,

        @NotNull(message = "Pick or optional is required")
        Boolean pick
) {
}
