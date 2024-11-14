package com.dev.dto.response;

import lombok.Builder;

import java.util.Set;


@Builder
public record FoodOptimizeResponse (
        Long id,
        String name,
        String price,
        String description,
        Set<String> images,
        Boolean available
){
}
