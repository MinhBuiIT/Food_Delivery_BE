package com.dev.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class CartResponse {
    Long id;
    Long totalPrice;
    List<CartItemResponse> items;
    Long restaurantId;

}
