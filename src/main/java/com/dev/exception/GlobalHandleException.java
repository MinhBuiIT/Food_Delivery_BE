package com.dev.exception;

import com.dev.core.ResponseError;
import com.dev.enums.ErrorEnum;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalHandleException {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseError> exception(Exception e) {
        String msg = e.getMessage();
        return ResponseEntity.status(500).body(new ResponseError(500,msg));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseError> accessDeniedException(
            AccessDeniedException e
    ) {
        String msg = e.getMessage();
        return ResponseEntity.status(403).body(new ResponseError(403,msg));
    }

    //Validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseError> methodArgumentNotValidException(
            MethodArgumentNotValidException e
    ) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(err -> {
            String errMsg = err.getDefaultMessage();
            String errField = err.getField();
            errors.put(errField, errMsg);
        });
        return ResponseEntity.status(422).body(new ResponseError(422,errors));
    }


    @ExceptionHandler(AppException.class)
    public ResponseEntity<ResponseError> handleAppException(
            AppException e
    ) {
        ErrorEnum error = e.getError();
        return ResponseEntity.status(error.getStatus()).body(new ResponseError(error.getStatus(), error.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseError> handleRuntimeException(
            RuntimeException e
    ) {
        String msg = e.getMessage();
        return ResponseEntity.badRequest().body(new ResponseError(400,msg));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ResponseError> handleJwtException(
            JwtException e
    ) {
        String msg = e.getMessage();
        return ResponseEntity.status(401).body(new ResponseError(401,msg));
    }
    @ExceptionHandler(AuthenticationServiceException.class)
    public ResponseEntity<ResponseError> handleAuthenticationServiceException(
            AuthenticationServiceException e
    ) {
        String msg = e.getMessage();
        return ResponseEntity.status(401).body(new ResponseError(401,msg));
    }

    //File not send
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ResponseError> handleMissingServletRequestPartException(
            MissingServletRequestPartException e
    ) {
        String msg = e.getMessage();
        return ResponseEntity.status(400).body(new ResponseError(400,msg));
    }
}
