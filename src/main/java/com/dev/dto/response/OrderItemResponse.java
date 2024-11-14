package com.dev.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemResponse {
    Long id;
    Integer quantity;
    FoodOptimizeResponse food;
    String specialInstructions;
    Long totalPrice;
    List<IngredientItemResponse> ingredients;
}
