package com.dev.dto.request;

import com.dev.enums.RoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record RegisterUserRequest(
        @NotBlank(message = "Email not empty")
        @Email(message = "Email is invalid")
        String email,

        @NotBlank(message = "Fullname not empty")
        String fullName,

        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
                message = "Minimum eight characters, at least one letter and one number")
        String password,

        @NotNull(message = "Not null")
        Boolean isRestaurant
) {
}
