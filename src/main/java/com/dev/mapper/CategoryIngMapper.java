package com.dev.mapper;

import com.dev.dto.response.CategoryFoodResponse;
import com.dev.dto.response.CategoryIngredientResponse;
import com.dev.models.CategoryFood;
import com.dev.models.CategoryIngredient;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryIngMapper {
    CategoryIngredientResponse toCategoryIngredientResponses(CategoryIngredient categoryIngredient);
}
