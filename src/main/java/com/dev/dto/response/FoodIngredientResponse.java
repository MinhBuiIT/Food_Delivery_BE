package com.dev.dto.response;

import lombok.*;

import java.util.List;
import java.util.Set;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodIngredientResponse{
    Long id;
    String name;
    String description;
    Set<String> images;
    Long price;
    List<CategoryIngredientWithListItem> ingredients;
}
