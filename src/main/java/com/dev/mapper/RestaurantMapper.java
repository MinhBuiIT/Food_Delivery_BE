package com.dev.mapper;

import com.dev.dto.response.RestaurantResponse;
import com.dev.models.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RestaurantMapper {
    @Mapping(target = "owner",ignore = true)
    @Mapping(target = "likes",source = "likes")
    RestaurantResponse toRestaurantResponse(Restaurant restaurant);
}
