package com.dev.dto.response;

import com.dev.enums.OrderStatus;
import com.dev.enums.PaymentEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse{

    Long id;
    UserResponse customer;
    String restaurant;
    AddressResponse address;
    Integer totalItem;
    Long totalPrice;
    OrderStatus orderStatus;
    Date createdAt;
    List<OrderItemResponse> orderItems;
    PaymentEnum payment;
}
