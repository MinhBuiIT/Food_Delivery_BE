package com.dev.controller;

import com.dev.core.ResponseSuccess;
import com.dev.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserController {

    UserService userService;

    @GetMapping("/profile")
    public ResponseSuccess getProfile() {
        var result = userService.myProfile();
        return ResponseSuccess.builder()
                .code(200)
                .metadata(result)
                .message("Get Profile Success")
                .build();
    }

}
