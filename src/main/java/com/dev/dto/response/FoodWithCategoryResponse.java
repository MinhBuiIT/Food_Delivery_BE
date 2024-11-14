package com.dev.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Builder
public record FoodWithCategoryResponse(
        String category,
        List<FoodCategoryResponse> foods
) {
}
