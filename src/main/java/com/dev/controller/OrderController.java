package com.dev.controller;

import com.dev.core.ResponseSuccess;
import com.dev.dto.request.AddCartItemRequest;
import com.dev.dto.request.CreateOrderRequest;
import com.dev.dto.request.UpdateQuantityCartItemRequest;
import com.dev.dto.request.UpdateStatusOrderRequest;
import com.dev.service.CartService;
import com.dev.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseSuccess createOrder(@RequestBody CreateOrderRequest request) {
        var result= orderService.createOrder(request);
        return ResponseSuccess.builder()
                .message("Create Order Success")
                .metadata(result)
                .code(HttpStatus.CREATED.value())
                .build();
    }

    @PostMapping("/{id}/status")
    public ResponseSuccess updateStatusOrder(
            @PathVariable("id") Long id,
            @RequestBody UpdateStatusOrderRequest request) {
        orderService.updateStatusOrder(id, request);
        return ResponseSuccess.builder()
                .message("Update Order Status Success")
                .code(HttpStatus.OK.value())
                .build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseSuccess cancelOrder(
            @PathVariable("id") Long id
    ) {
        orderService.cancelOrder(id);
        return ResponseSuccess.builder()
                .message("Cancel Order Status Success")
                .code(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/{status}/me")
    public ResponseSuccess getOrdersMe(
            @PathVariable("status") int status
    ) {
        var result= orderService.getOrderByUserByStatus(status);
        return ResponseSuccess.builder()
                .message("Get Orders Success")
                .metadata(result)
                .code(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/{id}")
    public ResponseSuccess getOrdersById(
            @PathVariable("id") Long orderId
    ) {
        var result= orderService.getOrderById(orderId);
        return ResponseSuccess.builder()
                .message("Get Order Success")
                .metadata(result)
                .code(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/{status}/restaurant")
    public ResponseSuccess getOrdersRestaurant(
            @PathVariable("status") int status
    ) {
        var result= orderService.getOrderByRestaurantByStatus(status);
        return ResponseSuccess.builder()
                .message("Get Restaurant Orders Success")
                .metadata(result)
                .code(HttpStatus.OK.value())
                .build();
    }
}
