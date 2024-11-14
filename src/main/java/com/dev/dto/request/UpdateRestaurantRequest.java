package com.dev.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Optional;

public record UpdateRestaurantRequest(
        Optional<String> name,

        Optional<String> description,

        Optional<String> cuisineType,

        Optional<String> mobile,

        Optional<String> facebook,

        Optional<String> instagram,

        Optional<String> primary_email,

        Optional<String> openingHours,

        Optional<String> numberStreet,

        Optional<String> street,

        Optional<String> ward,

        Optional<String> district,

        Optional<String> city,

        Optional<String> postalCode
) {
}
