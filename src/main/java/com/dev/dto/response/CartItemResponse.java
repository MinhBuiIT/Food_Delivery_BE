package com.dev.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartItemResponse {
    Long id;
    Integer quantity;
    FoodOptimizeResponse food;
    String specialInstructions;
    Long totalPrice;
    List<IngredientItemResponse> ingredients;
    EventResponse event;
}
