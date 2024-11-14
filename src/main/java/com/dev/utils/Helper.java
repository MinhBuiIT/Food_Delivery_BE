package com.dev.utils;

import com.dev.enums.ErrorEnum;
import com.dev.exception.AppException;
import com.dev.models.CategoryFood;
import com.dev.models.CategoryIngredient;
import com.dev.models.Restaurant;
import com.dev.repository.RestaurantRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class Helper {

    RestaurantRepository restaurantRepository;

    public Restaurant checkCategoryFoodExist(String email,String name) {
        Restaurant restaurant = restaurantRepository.findByOwnerEmailWithCategoryFood(email).orElse(null);
        if(restaurant == null) {
            throw new AppException(ErrorEnum.NOT_FOUND_OWNER);
        }
        Optional<CategoryFood> checkNameExist = restaurant.getCategoryFoods().stream()
                .filter(categoryFood -> categoryFood.getName().equals(name)).findFirst();
        if(checkNameExist.isPresent()) {
            throw new AppException(ErrorEnum.CATEGORY_FOOD_EXIST);
        }
        return restaurant;
    }

    public Restaurant checkCategoryIngredientExist(String email,String name,Boolean pick) {
        Restaurant restaurant = restaurantRepository.findByOwnerEmailWithCategoryIngredient(email).orElse(null);
        if(restaurant == null) {
            throw new AppException(ErrorEnum.NOT_FOUND_OWNER);
        }
        Optional<CategoryIngredient> checkNameExist = restaurant.getCategoryIngredients().stream()
                .filter(categoryIngredient -> categoryIngredient.getName().equals(name) && categoryIngredient.getPick().equals(pick)).findFirst();
        if(checkNameExist.isPresent()) {
            throw new AppException(ErrorEnum.CATEGORY_INGREDIENT_EXIST);
        }
        return restaurant;
    }
}
