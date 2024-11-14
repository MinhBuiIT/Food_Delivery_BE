package com.dev.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import javax.swing.text.html.Option;
import java.util.Optional;

public record IngredientItemUpdateRequest(
        Optional<String> name,

        @Positive(message = "Price is a positive number")
        Optional<Long> price
) {
}
