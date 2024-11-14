package com.dev.service;

import com.dev.dto.response.CategoryFoodResponse;
import com.dev.enums.ErrorEnum;
import com.dev.exception.AppException;
import com.dev.mapper.CategoryFoodMapper;
import com.dev.models.CategoryFood;
import com.dev.models.Restaurant;
import com.dev.repository.CategoryFoodRepository;
import com.dev.repository.RestaurantRepository;
import com.dev.utils.Helper;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class CategoryFoodService {

    CategoryFoodRepository categoryFoodRepository;
    CategoryFoodMapper categoryFoodMapper;
    RestaurantRepository restaurantRepository;
    Helper helper;

    @PreAuthorize("hasRole('RESTAURANT')")
    @Transactional
    public CategoryFoodResponse create(String name) {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        Restaurant restaurant = helper.checkCategoryFoodExist(email,name);

        CategoryFood newCategoryFood = CategoryFood.builder()
                .name(name)
                .build();
        restaurant.addCategoryFood(newCategoryFood);
        restaurantRepository.save(restaurant);
        return CategoryFoodResponse.builder().name(name).build();
    }

    @PreAuthorize("hasRole('RESTAURANT')")
    @Transactional
    public CategoryFoodResponse update(String name,Long id) {
        CategoryFood categoryFood = categoryFoodRepository.findById(id).orElse(null);
        if(categoryFood == null) {
            throw new AppException(ErrorEnum.CATEGORY_FOOD_NOT_FOUND);
        }
        //Kiểm tra name đã tồn tại chưa
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        helper.checkCategoryFoodExist(email,name);


        categoryFood.setName(name);
        categoryFoodRepository.save(categoryFood);
        return categoryFoodMapper.toCategoryFoodResponse(categoryFood);
    }

    public List<CategoryFoodResponse> getCategoriesOfRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findRestaurantWithCategory(restaurantId).orElse(null);
        if(restaurant == null) {
            throw new AppException(ErrorEnum.RES_NOT_FOUND);
        }
        //log.info("restaurantId: " + restaurantId);
        //log.info("ABC " + restaurant.getCategoryFoods().size());
        List<CategoryFoodResponse> responses = restaurant.getCategoryFoods().stream()
                .map(categoryFoodMapper::toCategoryFoodResponse).toList();
        return responses;
    }
}
