package com.dev.controller;


import com.dev.core.ResponseSuccess;
import com.dev.dto.request.CategoryFoodRequest;
import com.dev.service.CategoryFoodService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/category-food")
public class CategoryFoodController {

    CategoryFoodService categoryFoodService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseSuccess create(
            @RequestBody @Valid CategoryFoodRequest request
            ) {
        var result = categoryFoodService.create(request.name());
        return ResponseSuccess.builder()
                .message("Create category food successful")
                .code(HttpStatus.CREATED.value())
                .metadata(result)
                .build();
    }

    @GetMapping("/restaurant/{id}")
    public ResponseSuccess getCategoryFoodOfRestaurant(
            @PathVariable Long id
    ) {
        var result = categoryFoodService.getCategoriesOfRestaurant(id);
        return ResponseSuccess.builder()
                .message("Get category food successful")
                .code(HttpStatus.OK.value())
                .metadata(result)
                .build();
    }

    @PutMapping("/{id}")
    public ResponseSuccess update(
            @RequestBody @Valid CategoryFoodRequest request,
            @PathVariable Long id
    ) {
        var result = categoryFoodService.update(request.name(), id);
        return ResponseSuccess.builder()
                .message("Update category food successful")
                .code(HttpStatus.OK.value())
                .metadata(result)
                .build();
    }
}
