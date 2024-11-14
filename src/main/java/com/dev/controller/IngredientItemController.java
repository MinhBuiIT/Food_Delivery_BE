package com.dev.controller;

import com.dev.core.ResponseSuccess;
import com.dev.dto.request.IngredientItemRequest;
import com.dev.dto.request.IngredientItemUpdateRequest;
import com.dev.service.IngredientItemService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/ingredient-item")
public class IngredientItemController {

    IngredientItemService ingredientItemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseSuccess create(
            @RequestBody IngredientItemRequest request
            ) {
        var result = ingredientItemService.create(request);
        return ResponseSuccess.builder()
                .code(HttpStatus.CREATED.value())
                .metadata(result)
                .message("Create ingredient item success")
                .build();
    }

    @PutMapping("/{id}")
    public ResponseSuccess update(
            @PathVariable Long id,
            @RequestBody IngredientItemUpdateRequest request
    ) {
        var result = ingredientItemService.update(request,id);
        return ResponseSuccess.builder()
                .code(HttpStatus.OK.value())
                .metadata(result)
                .message("Update ingredient item success")
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseSuccess delete(
            @PathVariable Long id
    ) {
        ingredientItemService.remove(id);
        return ResponseSuccess.builder()
                .code(HttpStatus.OK.value())
                .message("Delete ingredient item success")
                .build();
    }

    @PostMapping("/{id}/status")
    public ResponseSuccess updateStatusStock(
            @PathVariable Long id
    ) {
        var result = ingredientItemService.updateStatusStock(id);
        return ResponseSuccess.builder()
                .code(HttpStatus.OK.value())
                .metadata(result)
                .message("Update stock ingredient item success")
                .build();
    }

    @GetMapping("/restaurant")
    public ResponseSuccess getIngredientItemByRestaurant() {
        var result = ingredientItemService.getAllIngredientItemByRestaurant();
        return ResponseSuccess.builder()
                .code(HttpStatus.OK.value())
                .metadata(result)
                .message("Get ingredient item success")
                .build();
    }
}
