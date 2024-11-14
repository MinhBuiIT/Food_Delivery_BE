package com.dev.dto.response;

import com.dev.models.Food;
import lombok.Builder;

import java.util.Set;

@Builder
public record CategoryFoodResponse(
        Long id,
        String name
) {
}
