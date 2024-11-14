package com.dev.dto.request;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateFoodRequest {
    String name;
    String description;
    Long price;
    Boolean vegetarian;
    Boolean seasonal;
    String categoryFood;
    Set<String> ingredients;
}
