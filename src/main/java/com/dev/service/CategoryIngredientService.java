package com.dev.service;

import com.dev.dto.response.CategoryFoodResponse;
import com.dev.dto.response.CategoryIngredientResponse;
import com.dev.enums.ErrorEnum;
import com.dev.exception.AppException;
import com.dev.mapper.CategoryIngMapper;
import com.dev.models.CategoryFood;
import com.dev.models.CategoryIngredient;
import com.dev.models.Restaurant;
import com.dev.repository.CategoryIngredientRepository;
import com.dev.repository.RestaurantRepository;
import com.dev.utils.Helper;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CategoryIngredientService {

    CategoryIngredientRepository categoryIngredientRepository;
    RestaurantRepository restaurantRepository;
    CategoryIngMapper categoryIngMapper;
    Helper helper;

    @PreAuthorize("hasRole('RESTAURANT')")
    @Transactional
    public CategoryIngredientResponse create(String name,Boolean pick) {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        Restaurant restaurant = helper.checkCategoryIngredientExist(email, name,pick);


        CategoryIngredient newCategoryIng = CategoryIngredient.builder()
                .name(name)
                .pick(pick)
                .build();
        restaurant.addCategoryIngredient(newCategoryIng);
        restaurantRepository.save(restaurant);
        return categoryIngMapper.toCategoryIngredientResponses(newCategoryIng);
    }

    @Transactional
    @PreAuthorize("hasRole('RESTAURANT')")
    public CategoryIngredientResponse update(String name,Boolean pick,Long id) {
        CategoryIngredient categoryIng = categoryIngredientRepository.findById(id).orElse(null);
        if(categoryIng == null) {
            throw new AppException(ErrorEnum.CATEGORY_INGREDIENT_NOT_FOUND);
        }
        //Kiểm tra name đã tồn tại chưa
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        Restaurant restaurant = helper.checkCategoryIngredientExist(email, name,pick);


        categoryIng.setName(name);
        categoryIng.setPick(pick);
        categoryIngredientRepository.save(categoryIng);
        return categoryIngMapper.toCategoryIngredientResponses(categoryIng);
    }

    @PreAuthorize("hasRole('RESTAURANT')")
    public List<CategoryIngredientResponse> getAllByRestaurant() {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        Restaurant restaurant = restaurantRepository.findByOwnerEmailWithCategoryIngredient(email).orElse(null);
        if(restaurant == null) {
            throw new AppException(ErrorEnum.RES_NOT_FOUND);
        }
        List<CategoryIngredientResponse> responses = restaurant.getCategoryIngredients()
                .stream().map(categoryIngMapper::toCategoryIngredientResponses).toList();
        return responses;
    }
}
