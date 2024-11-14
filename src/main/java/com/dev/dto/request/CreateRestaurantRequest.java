package com.dev.dto.request;

import com.dev.models.ContactInfo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record CreateRestaurantRequest(
        @NotBlank(message = "Name not empty")
        String name,

        @NotBlank(message = "Description not empty")
        String description,

        @NotBlank(message = "Cuisine type not empty")
        String cuisineType,

        @NotBlank(message = "mobile phone not empty")
        String mobile,

        @NotBlank(message = "facebook link not empty")
        String facebook,

        @NotBlank(message = "instagram link not empty")
        String instagram,

        @NotBlank(message = "Email restaurant not empty")
        @Email(message = "Email is invalid")
        String primary_email,

        @NotBlank(message = "Opening hours not empty")
        String openingHours,

        @NotBlank(message = "number street link not empty")
        String numberStreet,

        @NotBlank(message = "Street not empty")
        String street,

        @NotBlank(message = "Ward not empty")
        String ward,

        @NotBlank(message = "District not empty")
        String district,

        @NotBlank(message = "City not empty")
        String city,

        @NotBlank(message = "Postalcode not empty")
        String postalCode
) {
}
