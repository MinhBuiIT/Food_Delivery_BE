package com.dev.mapper;

import com.dev.dto.request.RegisterUserRequest;
import com.dev.dto.response.AuthResponse;
import com.dev.dto.response.TokenResponse;
import com.dev.dto.response.UserResponse;
import com.dev.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(RegisterUserRequest registerUserRequest);

    @Mapping(source = "tokenResponse",target = "token")
    AuthResponse toAuthResponseMapper(User user, TokenResponse tokenResponse);

    UserResponse toUserResponseMapper(User user);
}
