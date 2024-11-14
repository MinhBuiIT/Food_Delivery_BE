package com.dev.service;


import com.dev.dto.response.UserResponse;
import com.dev.enums.ErrorEnum;
import com.dev.exception.AppException;
import com.dev.mapper.UserMapper;
import com.dev.models.User;
import com.dev.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    UserRepository userRepository;
    UserMapper userMapper;

    public UserResponse myProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            throw new AppException(ErrorEnum.NOT_FOUND_USER);
        }
        return userMapper.toUserResponseMapper(user);

    }

}
