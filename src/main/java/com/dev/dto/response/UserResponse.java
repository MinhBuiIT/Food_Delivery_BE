package com.dev.dto.response;

import com.dev.models.Address;
import com.dev.models.Restaurant;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponse(
        Long id,
        String fullName,
        String email,
        Set<Address> addresses
) {
}
