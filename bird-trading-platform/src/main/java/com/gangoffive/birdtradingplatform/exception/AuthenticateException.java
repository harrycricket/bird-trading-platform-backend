package com.gangoffive.birdtradingplatform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthenticateException extends AuthenticationException {
    public AuthenticateException(String message) {
        super(message);
    }
}
