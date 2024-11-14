package com.dev.controller;

import com.dev.core.ResponseSuccess;
import com.dev.dto.request.LoginUserRequest;
import com.dev.dto.request.RefreshTokenRequest;
import com.dev.dto.request.RegisterUserRequest;
import com.dev.service.AuthenticateService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthenticateController {

    AuthenticateService authenticateService;

    @GetMapping
    public ResponseEntity<String> authenticate() {
        return new ResponseEntity<>("ABC", HttpStatus.OK);
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseSuccess signup(
            @RequestBody RegisterUserRequest registerUserRequest
    ){
       var result = authenticateService.signupUser(registerUserRequest);
       return ResponseSuccess.builder()
               .code(HttpStatus.CREATED.value())
               .message("Created User Successfully")
               .metadata(result)
               .build();
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseSuccess signup(
            @RequestBody LoginUserRequest loginUserRequest
    ){
        var result = authenticateService.signinUser(loginUserRequest);
        return ResponseSuccess.builder()
                .code(HttpStatus.OK.value())
                .message("Login Successfully")
                .metadata(result)
                .build();
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public ResponseSuccess logout(){
        var msg = authenticateService.logoutUser();
        return ResponseSuccess.builder()
                .code(HttpStatus.OK.value())
                .message(msg)
                .build();
    }

    @PostMapping("/refresh-token")
    @ResponseStatus(HttpStatus.OK)
    public ResponseSuccess refreshToken(
            @RequestBody RefreshTokenRequest request
    ) throws ParseException, JOSEException {
        var result = authenticateService.refreshToken(request);
        return ResponseSuccess.builder()
                .code(HttpStatus.OK.value())
                .message("Refresh token successfully")
                .metadata(result)
                .build();
    }
}
