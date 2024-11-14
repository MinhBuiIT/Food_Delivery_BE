package com.dev.mapper;

import com.dev.dto.response.CartItemResponse;
import com.dev.dto.response.OrderItemResponse;
import com.dev.dto.response.OrderResponse;
import com.dev.models.CartItem;
import com.dev.models.Order;
import com.dev.models.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "ingredients", ignore = true)
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);
}
