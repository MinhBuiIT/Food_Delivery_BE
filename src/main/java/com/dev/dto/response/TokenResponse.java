package com.dev.dto.response;

import lombok.Builder;

@Builder
public record TokenResponse(
        String access_token,
        String refresh_token
) {
}
