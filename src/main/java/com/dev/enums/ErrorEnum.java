package com.dev.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorEnum {
    USER_EXIST(400,"User already exists"),
    FAILED_LOGIN(401,"Login failed"),
    NOT_FOUND_USER(404,"User not found"),
    UNAUTHENTICATED(401, "Unauthenticated"),
    TOKEN_EXPIRE(401, "Token is expired"),
    NOT_FOUND_OWNER(404, "Owner not found"),
    USER_CREATED_RES(400,"User created restaurant"),
    RES_ADDRESS_EXIST(400,"Address of restaurant is exist"),
    RES_FILE_IMAGES(400,"Image files not found"),
    RES_NOT_FOUND(400,"Restaurant not found"),
    RES_DISABLE(403,"Restaurant is disabled"),
    CATEGORY_FOOD_EXIST(400,"Category food is exist"),
    CATEGORY_FOOD_NOT_FOUND(404,"Category food not found"),
    CATEGORY_INGREDIENT_EXIST(400,"Category ingredient is exist"),
    CATEGORY_INGREDIENT_NOT_FOUND(404,"Category ingredient not found"),
    INGREDIENT_ITEM_EXIST(400,"Ingredient item is exist in category item"),
    INGREDIENT_ITEM_NOT_FOUND(404,"Ingredient item not found"),
    INGREDIENT_ITEM_ADDED_FOOD(400,"Ingredient item has added into food"),
    FOOD_INGREDIENT_INVALID(400,"Ingredient food is invalid"),
    FOOD_EXIST(400,"Food is exist"),
    FOOD_FILE_IMAGE(400,"Image file is invalid"),
    FOOD_NOT_FOUND(400,"Food not found"),
    FOOD_INVALID(400,"Food is invalid"),
    FOOD_NOT_AVAILABLE(400,"Food isn't available"),
    CART_NOT_FOUND(404,"Cart not found"),
    CART_EMPTY(400,"Cart is empty"),
    CART_FOOD_OTHER_RESTAURANT(400,"Please clear cart or order before add this food into cart"),
    FOOD_NOT_IN_CART(404,"Food not in cart"),
    INGREDIENTS_INVALID(400,"Ingredients is invalid"),
    INGREDIENT_NOT_STOCK(400,"Ingredient is not stock"),
    ADDRESS_NOT_FOUND(404,"Address not found"),
    ADDRESS_EXIST(400,"Address is exist"),
    ADDRESS_DEFAULT_NOT_FOUND(404,"Address default not found"),
    ORDER_NOT_FOUND(404,"Order not found"),
    ORDER_STATUS_INVALID(400,"Order status is invalid"),
    ORDER_HAS_CONFIRMED(400,"Order has confirmed"),
    FOOD_HAS_VOUCHER(400,"Food has applied to voucher"),
    EVENT_TIME_INVALID(400,"Event time is invalid"),
    EVENT_NOT_FOUND(404,"Event not found")
    ;
    int status;
    String message;



}
