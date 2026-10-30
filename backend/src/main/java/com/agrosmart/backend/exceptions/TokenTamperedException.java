package com.agrosmart.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class TokenTamperedException extends RuntimeException {
    public TokenTamperedException(String message) {
        super(message);
    }
}
