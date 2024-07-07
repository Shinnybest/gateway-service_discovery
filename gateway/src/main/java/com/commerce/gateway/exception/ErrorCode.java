package com.commerce.gateway.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    FAILED_VERIFY_TOKEN(HttpStatus.UNAUTHORIZED, "001", "Token verification failed"),
    FAILED_EXTRACT_PAYLOAD(HttpStatus.UNAUTHORIZED, "002", "Failed to extract payload");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

