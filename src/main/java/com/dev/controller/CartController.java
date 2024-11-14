package com.dev.controller;

import com.dev.core.ResponseSuccess;
import com.dev.dto.request.AddCartItemRequest;
import com.dev.dto.request.UpdateQuantityCartItemRequest;
import com.dev.service.CartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    CartService cartService;

    @PostMapping("/add")
    ResponseSuccess addItemIntoCart(
        @RequestBody AddCartItemRequest request
    ) {
        cartService.addCartItem(request);

        return ResponseSuccess.builder()
                .message("Add item into cart success")
                .code(HttpStatus.OK.value())
                .build();
    }

    @GetMapping
    ResponseSuccess getAllCartItems() {
        var result = cartService.getAllItemInCart();
        return ResponseSuccess.builder()
                .message("Get all item in cart success")
                .code(HttpStatus.OK.value())
                .metadata(result)
                .build();
    }

    @PostMapping("/update-quantity")
    ResponseSuccess updateQuantity(
            @RequestBody UpdateQuantityCartItemRequest request
            ) {
        var result = cartService.updateCartItemQuantity(request);
        return ResponseSuccess.builder()
                .message("Update quantity item in cart success")
                .code(HttpStatus.OK.value())
                .metadata(result)
                .build();
    }

    @DeleteMapping("/item/{id}")
    ResponseSuccess deleteItemFromCart(
            @PathVariable long id
    ) {
        cartService.removeItemFromCart(id);
        return ResponseSuccess.builder()
                .message("Remove item in cart success")
                .code(HttpStatus.OK.value())
                .build();
    }

    @DeleteMapping("/clear")
    ResponseSuccess clearCart() {
        cartService.clearCart();
        return ResponseSuccess.builder()
                .message("Clear all item in cart success")
                .code(HttpStatus.OK.value())
                .build();
    }
}
