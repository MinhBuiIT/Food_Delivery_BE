package com.dev.dto.request;

import com.dev.enums.PaymentEnum;

public record CreateOrderRequest (
        Long restaurantId,
        Long addressId,
        PaymentEnum payment
) {
}
