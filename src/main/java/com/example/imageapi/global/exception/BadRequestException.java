package com.example.imageapi.global.exception;

import lombok.Getter;

import java.io.IOException;

@Getter
public class BadRequestException extends RuntimeException{
    private final int code;
    private final String message;

    public BadRequestException(final ExceptionCode exceptionCode) {
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getMessage();
    }

    public BadRequestException(final ExceptionCode exceptionCode, final IOException cause) {
        super(cause);
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getMessage();
    }
}
