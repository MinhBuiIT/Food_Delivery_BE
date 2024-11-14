package com.dev.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateAddressRequest(

        @NotBlank(message = "Number street is required")
        String numberStreet,


        @NotBlank(message = "Street is required")
        String street,

        @NotBlank(message = "Ward is required")
        String ward,


        @NotBlank(message = "District is required")
        String district,

        @NotBlank(message = "City is required")
        String city,

        @NotBlank(message = "PostalCode is required")
        String postalCode,

        @NotBlank(message = "Phone is required")
        String phone
) {
}
