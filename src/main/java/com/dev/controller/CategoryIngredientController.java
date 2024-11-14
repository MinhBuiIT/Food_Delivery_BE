package com.dev.controller;


import com.dev.core.ResponseSuccess;
import com.dev.dto.request.CategoryFoodRequest;
import com.dev.dto.request.CategoryIngredientRequest;
import com.dev.models.CategoryIngredient;
import com.dev.service.CategoryFoodService;
import com.dev.service.CategoryIngredientService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/category-ingredient")
public class CategoryIngredientController {

    CategoryIngredientService categoryIngredientService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseSuccess create(
            @RequestBody @Valid CategoryIngredientRequest request
            ) {
        var result = categoryIngredientService.create(request.name(),request.pick());
        return ResponseSuccess.builder()
                .message("Create category ingredient successful")
                .code(HttpStatus.CREATED.value())
                .metadata(result)
                .build();
    }


    @PutMapping("/{id}")
    public ResponseSuccess update(
            @RequestBody @Valid CategoryIngredientRequest request,
            @PathVariable Long id
    ) {
        var result = categoryIngredientService.update(request.name(),request.pick(), id);
        return ResponseSuccess.builder()
                .message("Update category ingredient successful")
                .code(HttpStatus.OK.value())
                .metadata(result)
                .build();
    }

    @GetMapping("/restaurant")
    public ResponseSuccess getAllByRestaurant() {
        var result = categoryIngredientService.getAllByRestaurant();
        return ResponseSuccess.builder()
                .message("Get category ingredient successful")
                .code(HttpStatus.OK.value())
                .metadata(result)
                .build();
    }

}
