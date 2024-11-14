package com.dev.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginUserRequest(

        @NotBlank(message = "Email not empty")
        @Email(message = "Email is invalid")
        String email,
        String password
) {
}
