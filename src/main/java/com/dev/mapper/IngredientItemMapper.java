package com.dev.mapper;

import com.dev.dto.request.IngredientItemRequest;
import com.dev.dto.response.IngredientItemFood;
import com.dev.dto.response.IngredientItemResponse;
import com.dev.dto.response.IngredientItemRestaurantResponse;
import com.dev.models.CategoryIngredient;
import com.dev.models.IngredientItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IngredientItemMapper {
    @Mapping(target = "categoryIngredient", ignore = true)
    IngredientItemResponse toIngredientItemResponse(IngredientItem ingredientItem);

    @Mapping(target = "categoryIngredient", ignore = true)
    IngredientItem toIngredientItem(IngredientItemRequest ingredientItemRequest);

    IngredientItemRestaurantResponse toIngredientItemRestaurantResponse(CategoryIngredient categoryIngredient);

    IngredientItemFood toIngredientItemFood(IngredientItem ingredientItem);
}
