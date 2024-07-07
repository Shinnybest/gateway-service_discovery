package com.commerce.gateway.exception;

import lombok.Getter;

@Getter
public class AuthenticationException extends CustomException {
    public AuthenticationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
