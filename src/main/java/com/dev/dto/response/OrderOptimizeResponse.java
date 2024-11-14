package com.dev.dto.response;

import com.dev.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderOptimizeResponse {
    Long id;
    String restaurant;
    Long totalPrice;
    OrderStatus orderStatus;
    Date createdAt;
}
