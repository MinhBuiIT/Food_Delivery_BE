package com.dev.mapper;

import com.dev.dto.response.CategoryFoodResponse;
import com.dev.dto.response.RestaurantResponse;
import com.dev.models.CategoryFood;
import com.dev.models.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryFoodMapper {

    CategoryFoodResponse toCategoryFoodResponse(CategoryFood categoryFood);
}
