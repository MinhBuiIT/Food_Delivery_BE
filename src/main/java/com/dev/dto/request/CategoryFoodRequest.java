package com.dev.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategoryFoodRequest(
        @NotBlank(message = "Category food's name must not be empty")
        String name
) {
}
