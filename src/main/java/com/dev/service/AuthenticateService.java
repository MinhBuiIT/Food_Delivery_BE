package com.dev.service;

import com.dev.dto.request.LoginUserRequest;
import com.dev.dto.request.RefreshTokenRequest;
import com.dev.dto.request.RegisterUserRequest;
import com.dev.dto.response.TokenResponse;
import com.dev.dto.response.AuthResponse;
import com.dev.enums.ErrorEnum;
import com.dev.enums.RoleEnum;
import com.dev.exception.AppException;
import com.dev.mapper.UserMapper;
import com.dev.models.Cart;
import com.dev.models.Restaurant;
import com.dev.models.Token;
import com.dev.models.User;
import com.dev.repository.CartRepository;
import com.dev.repository.RestaurantRepository;
import com.dev.repository.TokenRepository;
import com.dev.repository.UserRepository;
import com.dev.utils.AuthUtil;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticateService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final TokenRepository tokenRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthUtil authUtil;

    @Value("${jwt.secret.access_token}")
    private String accessTokenSecret;

    @Value("${jwt.secret.refresh_token}")
    private String refreshTokenSecret;

    private int expireAccessToken = 1;

    private int expireRefreshToken = 7;

    @Transactional
    public AuthResponse signupUser(
            RegisterUserRequest registerUserRequest
    )  {
        String email = registerUserRequest.email();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            throw new AppException(ErrorEnum.USER_EXIST);
        }
        User newUserMapp = userMapper.toUser(registerUserRequest);
        newUserMapp.setPassword(passwordEncoder.encode(newUserMapp.getPassword()));
        RoleEnum role = registerUserRequest.isRestaurant() ? RoleEnum.ROLE_RESTAURANT : RoleEnum.ROLE_USER;
        newUserMapp.setRole(role);

        User newUser =  userRepository.save(newUserMapp);

        //generate token
        String accessToken = authUtil.generateToken(newUser,expireAccessToken,accessTokenSecret,null);
        String refreshToken = authUtil.generateToken(newUser,expireRefreshToken,refreshTokenSecret,null);
        //save refresh token into token entity
        tokenRepository.save(Token.builder()
                        .user(newUser)
                        .refreshToken(refreshToken)
                .build());


        if (!registerUserRequest.isRestaurant()) {
            //create cart
            Cart cart = Cart.builder()
                    .customer(newUser)
                    .totalPrice(0L)
                    .build();
            cartRepository.save(cart);
        }else {
            //create owner
            restaurantRepository.save(Restaurant.builder()
                    .owner(newUser)
                    .build());
        }




        TokenResponse tokenResponse =TokenResponse.builder()
                .access_token(accessToken)
                .refresh_token(refreshToken)
                .build();
        AuthResponse userResponse = userMapper.toAuthResponseMapper(newUser,tokenResponse);
        return userResponse;
    }


    public AuthResponse signinUser(
            LoginUserRequest request
    ) {
        User user = userRepository.findByEmail(request.email()).orElse(null);
        if(user == null) {
            throw new  AppException(ErrorEnum.FAILED_LOGIN);
        }
        boolean isMatch = passwordEncoder.matches(request.password(), user.getPassword());
        if(!isMatch) {
            throw new AppException(ErrorEnum.FAILED_LOGIN);
        }
        //generate token
        String accessToken = authUtil.generateToken(user,expireAccessToken,accessTokenSecret,null);
        String refreshToken = authUtil.generateToken(user,expireRefreshToken,refreshTokenSecret,null);

        Token token = tokenRepository.findByUser(user).orElse(null);
        if(token == null) {
            tokenRepository.save(Token.builder()
                    .user(user)
                    .refreshToken(refreshToken)
                    .build());
        }else {
            token.setRefreshToken(refreshToken);
            tokenRepository.save(token);
        }
        TokenResponse tokenResponse =TokenResponse.builder()
                .access_token(accessToken)
                .refresh_token(refreshToken)
                .build();
        AuthResponse userResponse = userMapper.toAuthResponseMapper(user,tokenResponse);
        return userResponse;

    }

    public String logoutUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var email = auth.getName();
        //xóa token trong db
        User user = userRepository.findByEmail(email).orElse(null);

        if(user == null) {
            throw new  AppException(ErrorEnum.NOT_FOUND_USER);
        }
        log.info("EMAIL " + user.getEmail());
        tokenRepository.deleteTokenByUser(user);
        return "Logout successfully";
    }


    @Transactional
    public TokenResponse refreshToken(RefreshTokenRequest request) throws ParseException, JOSEException {
        String token = request.refresh_token();
        SignedJWT signedJWT = authUtil.verifyToken(token,refreshTokenSecret);
        //kiểm tra token trong db
        Token refreshTokenEntity = tokenRepository.findTokenByRefreshToken(token).orElse(null);
        if(refreshTokenEntity == null) {
            throw new AppException(ErrorEnum.UNAUTHENTICATED);
        }
        User user = userRepository.findById(refreshTokenEntity.getUser().getId()).orElse(null);
        if(user == null) {
            throw new AppException(ErrorEnum.NOT_FOUND_USER);
        }
        Date expireTimeOfRefreshToken = signedJWT.getJWTClaimsSet().getExpirationTime();
        String accessToken = authUtil.generateToken(user,expireAccessToken,accessTokenSecret,null);
        String refreshToken = authUtil.generateToken(user,expireRefreshToken,refreshTokenSecret,expireTimeOfRefreshToken);

        //cập nhật token trong db
        refreshTokenEntity.setRefreshToken(refreshToken);
        tokenRepository.save(refreshTokenEntity);
        TokenResponse tokenResponse =TokenResponse.builder()
                .access_token(accessToken)
                .refresh_token(refreshToken)
                .build();
        return tokenResponse;
    }
}
