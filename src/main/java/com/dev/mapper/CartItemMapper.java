package com.dev.mapper;

import com.dev.dto.request.AddCartItemRequest;
import com.dev.dto.response.CartItemResponse;
import com.dev.models.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    CartItem toCartItemFromRequest(AddCartItemRequest request);

    @Mapping(target = "ingredients", ignore = true)
    CartItemResponse toCartItemResponse(CartItem cartItem);
}
