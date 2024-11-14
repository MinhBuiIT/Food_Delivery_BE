package com.dev.controller;

import com.dev.core.ResponseSuccess;
import com.dev.dto.request.CreateRestaurantRequest;
import com.dev.dto.request.UpdateRestaurantRequest;
import com.dev.enums.ErrorEnum;
import com.dev.exception.AppException;
import com.dev.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/restaurant")
public class RestaurantController {

    RestaurantService restaurantService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseSuccess createRestaurant(
            @Valid @ModelAttribute CreateRestaurantRequest request,
            @RequestParam List<MultipartFile> files
            ) throws IOException {

        var result = restaurantService.create(request,files);
        return ResponseSuccess.builder()
                .message("Create restaurant success")
                .code(HttpStatus.CREATED.value())
                .metadata(result)
                .build();
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseSuccess updateRestaurant(
            @ModelAttribute UpdateRestaurantRequest request,
            @PathVariable Long id,
            @RequestParam Optional<List<MultipartFile>> files
    ) throws IOException {

        var result = restaurantService.update(request,files,id);
        return ResponseSuccess.builder()
                .message("Update restaurant success")
                .code(HttpStatus.OK.value())
                .metadata(result)
                .build();
    }

    @PostMapping("/{id}/disable")
    @ResponseStatus(HttpStatus.OK)
    public ResponseSuccess updateDisableRestaurant(
            @PathVariable Long id
    ) {

         restaurantService.updateDisableRestaurant(id);
        return ResponseSuccess.builder()
                .message("Update disable restaurant success")
                .code(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/by-owner")
    public ResponseSuccess getRestaurantByOwner() {
        var result = restaurantService.getRestaurantByOwner();
        return ResponseSuccess.builder()
                .message("Get restaurant success")
                .code(HttpStatus.OK.value())
                .metadata(result)
                .build();
    }

    @PostMapping("/{id}/change-status")
    public ResponseSuccess changeRestaurantStatus(
            @PathVariable Long id
    ) {
        var result = restaurantService.changeStatusRestaurant(id);
        return ResponseSuccess.builder()
                .message("Change status success")
                .code(HttpStatus.OK.value())
                .metadata(result)
                .build();
    }

    @GetMapping("/all")
    public ResponseSuccess getAllRestaurant(
            @RequestParam(defaultValue = "1") String page,
            @RequestParam(defaultValue = "10") String size
    ) {
        var result = restaurantService.getAllRestaurants(Integer.parseInt(page) ,Integer.parseInt(size));
        return ResponseSuccess.builder()
                .message("Get all restaurants success")
                .code(HttpStatus.OK.value())
                .metadata(result)
                .build();
    }
    @GetMapping("/total-page")
    public ResponseSuccess getTotalPageRestaurant(
            @RequestParam(defaultValue = "1") String page,
            @RequestParam(defaultValue = "10") String size
    ) {
        var result = restaurantService.getTotalPages(Integer.parseInt(page) ,Integer.parseInt(size));
        return ResponseSuccess.builder()
                .message("Get total page restaurants success")
                .code(HttpStatus.OK.value())
                .metadata(result)
                .build();
    }

    @GetMapping("/{id}")
    public ResponseSuccess getRestaurant(@PathVariable Long id) {
        var result = restaurantService.getRestaurantById(id);
        return ResponseSuccess.builder()
                .message("Get a restaurant success")
                .code(HttpStatus.OK.value())
                .metadata(result)
                .build();
    }

    @GetMapping("/search")
    public ResponseSuccess getRestaurantSearch(
            @RequestParam String key
    ) {
        var result = restaurantService.getRestaurantsBySearch(key);
        return ResponseSuccess.builder()
                .message("Search restaurant success")
                .code(HttpStatus.OK.value())
                .metadata(result)
                .build();
    }

    @PostMapping("/{id}/like")
    public ResponseSuccess likeRestaurant(
            @PathVariable Long id
    ) {
        var result = restaurantService.addToFavorites(id);
        return ResponseSuccess.builder()
                .message(!result ? "Like restaurant success" : "Unlike restaurant success")
                .code(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("/likes")
    public ResponseSuccess getLikes() {
        var result = restaurantService.getAllFavorites();
        return ResponseSuccess.builder()
                .message("Get all liked restaurant success")
                .code(HttpStatus.OK.value())
                .metadata(result)
                .build();
    }
}
