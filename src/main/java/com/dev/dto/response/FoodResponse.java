package com.dev.dto.response;

import com.dev.models.IngredientItem;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FoodResponse {
    Long id;
    String name;
    String description;
    Long price;
    Boolean vegetarian;
    Boolean seasonal;
    Boolean available;
    boolean disable;
    Date createdAt;
    Set<String> images;
    CategoryFoodResponse categoryFood;
    Set<IngredientItemResponse> ingredients;
    Boolean disabled;
}
