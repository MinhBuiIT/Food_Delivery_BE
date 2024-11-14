package com.dev.dto.response;

import com.dev.enums.RoleEnum;


public record AuthResponse(
        Long id,
        String email,
        String fullName,
        RoleEnum role,
        TokenResponse token
) {

}


