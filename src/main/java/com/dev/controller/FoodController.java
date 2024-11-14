package com.dev.controller;

import com.dev.core.ResponseSuccess;
import com.dev.dto.request.CreateFoodRequest;
import com.dev.service.FoodService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/food")
public class FoodController {
    FoodService foodService;

    @PostMapping
    public ResponseSuccess create(
            @RequestParam MultipartFile file,
            @RequestParam("data") String data
    ) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        CreateFoodRequest createFoodRequest = mapper.readValue(data, CreateFoodRequest.class);
        var result = foodService.addFood(createFoodRequest,file);

        return ResponseSuccess.builder()
                .message("Create Food success")
                .code(HttpStatus.CREATED.value())
                .metadata(result)
                .build();

    }

    @PostMapping("/{id}/disable")
    public ResponseSuccess changeDisableFood(
            @PathVariable long id
    ) {
        foodService.changeDisableFood(id);
        return ResponseSuccess.builder()
                .message("Change Status Food success")
                .code(HttpStatus.OK.value())
                .build();
    }

    @PostMapping("/{id}/available")
    public ResponseSuccess changeAvailableFood(
            @PathVariable long id
    ) {
        foodService.updateFoodAvailabilityStatus(id);
        return ResponseSuccess.builder()
                .message("Change Available Food success")
                .code(HttpStatus.OK.value())
                .build();
    }


    @GetMapping("/restaurant/all")
    public ResponseSuccess getFoodRestaurant() {

        var result = foodService.getRestaurantFoodsAll();
        return ResponseSuccess.builder()
                .message("Get All Food Restaurant success")
                .code(HttpStatus.OK.value())
                .metadata(result)
                .build();
    }

    @GetMapping("/restaurant/{id}/detail")
    public ResponseSuccess getFoodDetailsRestaurant(@PathVariable Long id) {
        var result = foodService.getFoodDetailRestaurant(id);
        return ResponseSuccess.builder()
                .message("Get Detail Food Restaurant success")
                .code(HttpStatus.OK.value())
                .metadata(result)
                .build();
    }


    @GetMapping("/restaurant/{id}")
    public ResponseSuccess getFoodRestaurant(
            @PathVariable Long id,
             @RequestParam Optional<Boolean> vegetarian,
             @RequestParam Optional<Boolean> seasonal

    ) {

        var result = foodService.getRestaurantFoods(id,vegetarian,seasonal);
        return ResponseSuccess.builder()
                .message("Get Food Restaurant success")
                .code(HttpStatus.OK.value())
                .metadata(result)
                .build();
    }

    @GetMapping("/{id}/ingredients")
    public ResponseSuccess getFoodIngredients(
            @PathVariable Long id
    ) {
        var result = foodService.getIngredientOfFood(id);

        return ResponseSuccess.builder()
                .message("Get Ingredient Food success")
                .code(HttpStatus.OK.value())
                .metadata(result)
                .build();
    }
}
