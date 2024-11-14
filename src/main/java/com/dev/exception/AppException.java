package com.dev.exception;

import com.dev.enums.ErrorEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Builder
public class AppException extends RuntimeException {

    ErrorEnum error;

    public AppException(ErrorEnum error) {
        super(error.getMessage());
        this.error = error;
    }

    public ErrorEnum getError() {
        return error;
    }

    public void setError(ErrorEnum error) {
        this.error = error;
    }
}
