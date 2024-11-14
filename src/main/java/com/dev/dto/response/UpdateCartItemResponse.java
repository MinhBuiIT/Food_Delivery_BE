package com.dev.dto.response;


import lombok.Builder;

@Builder
public record UpdateCartItemResponse(
        Long id,
        Long totalPrice,
        CartItemResponse updateCartItem
) {
}
