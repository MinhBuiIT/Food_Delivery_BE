package com.dev.mapper;

import com.dev.dto.response.OrderResponse;
import com.dev.models.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "restaurant", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    OrderResponse toOrderResponses(Order order);
}
