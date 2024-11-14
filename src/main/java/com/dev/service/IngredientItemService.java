package com.dev.service;

import com.dev.dto.request.IngredientItemRequest;
import com.dev.dto.request.IngredientItemUpdateRequest;
import com.dev.dto.response.IngredientItemResponse;
import com.dev.dto.response.IngredientItemRestaurantResponse;
import com.dev.enums.ErrorEnum;
import com.dev.exception.AppException;
import com.dev.mapper.CategoryFoodMapper;
import com.dev.mapper.CategoryIngMapper;
import com.dev.mapper.IngredientItemMapper;
import com.dev.models.CategoryIngredient;
import com.dev.models.IngredientItem;
import com.dev.models.Restaurant;
import com.dev.repository.CategoryIngredientRepository;
import com.dev.repository.IngredientItemRepository;
import com.dev.repository.RestaurantRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class IngredientItemService {

    IngredientItemRepository ingredientItemRepository;
    CategoryIngredientRepository categoryIngredientRepository;
    IngredientItemMapper ingredientItemMapper;
    CategoryIngMapper categoryIngMapper;
    RestaurantRepository restaurantRepository;

    @Transactional
    @PreAuthorize("hasRole('RESTAURANT')")
    public IngredientItemResponse create(
            IngredientItemRequest ingredientItemRequest
    ) {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        Restaurant restaurant = restaurantRepository.findByOwnerEmailWithCategoryIngredient(email).orElse(null);
        if (restaurant == null) {
            throw new AppException(ErrorEnum.NOT_FOUND_OWNER);
        }

        //Kiểm tra name CategoryIngredient
        CategoryIngredient categoryIngredient = restaurant.getCategoryIngredients().stream()
                .filter(categoryIngredient1 -> categoryIngredient1.getName().equals(ingredientItemRequest.categoryIngredient()))
                .findFirst()
                .orElse(null);
        if (categoryIngredient == null) {
            throw new AppException(ErrorEnum.CATEGORY_INGREDIENT_NOT_FOUND);
        }

        //Kiểm tra name đã tồn tại trong CategoryIngredient
        Optional<IngredientItem> ingredientItem = categoryIngredient.getIngredients().stream()
                .filter(ingredientItem1 -> ingredientItem1.getName().equals(ingredientItemRequest.name()))
                .findFirst();
        if (ingredientItem.isPresent()) {
            throw new AppException(ErrorEnum.INGREDIENT_ITEM_EXIST);
        }

        IngredientItem newIngredientItem = IngredientItem.builder()
                .name(ingredientItemRequest.name())
                .price(ingredientItemRequest.price())
                .isStock(true)
                .build();
        categoryIngredient.addIngredient(newIngredientItem);
        categoryIngredientRepository.save(categoryIngredient);

        IngredientItemResponse ingredientItemResponse = ingredientItemMapper.toIngredientItemResponse(newIngredientItem);
        ingredientItemResponse.setStock(true);
        ingredientItemResponse.setCategoryIngredient(categoryIngMapper.toCategoryIngredientResponses(categoryIngredient));
        return ingredientItemResponse;
    }

    @Transactional
    @PreAuthorize("hasRole('RESTAURANT')")
    public IngredientItemResponse update(
            IngredientItemUpdateRequest request,
            Long id
    ) {
        IngredientItem ingredientItem = ingredientItemRepository.findById(id).orElse(null);
        if (ingredientItem == null) {
            throw new AppException(ErrorEnum.INGREDIENT_ITEM_NOT_FOUND);
        }

        if(request.name().isPresent()) {
            ingredientItem.setName(request.name().get());
        }
        if(request.price().isPresent()) {
            ingredientItem.setPrice(request.price().get());
        }
        ingredientItemRepository.save(ingredientItem);
        var ingredientResponse = IngredientItemResponse.builder()
                .name(request.name().isPresent() ? request.name().get() : ingredientItem.getName())
                .price(request.price().isPresent() ? request.price().get() : ingredientItem.getPrice())
                .categoryIngredient(categoryIngMapper.toCategoryIngredientResponses(ingredientItem.getCategoryIngredient()))
                .stock(ingredientItem.isStock())
                .build();
        return ingredientResponse;
    }

    @Transactional
    public void remove(Long id) {
        IngredientItem ingredientItem = ingredientItemRepository.findByIdWithCategoryIngredient(id).orElse(null);
        if (ingredientItem == null) {
            throw new AppException(ErrorEnum.INGREDIENT_ITEM_NOT_FOUND);
        }
        if(!ingredientItem.getFoods().isEmpty()) {
            throw new AppException(ErrorEnum.INGREDIENT_ITEM_ADDED_FOOD);
        }

        CategoryIngredient categoryIngredient = ingredientItem.getCategoryIngredient();
        categoryIngredient.removeIngredient(ingredientItem);
        //categoryIngredientRepository.save(categoryIngredient);
        ingredientItemRepository.delete(ingredientItem);
    }

    @Transactional
    public IngredientItemResponse updateStatusStock(Long id) {
        IngredientItem ingredientItem = ingredientItemRepository.findByIdWithCategoryIngredient(id).orElse(null);
        if (ingredientItem == null) {
            throw new AppException(ErrorEnum.INGREDIENT_ITEM_NOT_FOUND);
        }
        ingredientItem.setStock(!ingredientItem.isStock());
        var updatedIngredientItem = ingredientItemRepository.save(ingredientItem);
        var response = ingredientItemMapper.toIngredientItemResponse(updatedIngredientItem);
        response.setStock(updatedIngredientItem.isStock());
        response.setCategoryIngredient(categoryIngMapper.toCategoryIngredientResponses(updatedIngredientItem.getCategoryIngredient()));
        return response;
    }

    public List<IngredientItemRestaurantResponse> getAllIngredientItemByRestaurant() {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<CategoryIngredient> categoryIngredient = categoryIngredientRepository.findByRestaurantEmail(email);


        List<IngredientItemRestaurantResponse> responses = categoryIngredient.stream()
                .map(ingredientItemMapper::toIngredientItemRestaurantResponse).toList();

        return responses;
    }

}
