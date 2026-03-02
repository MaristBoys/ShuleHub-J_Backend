package com.shulehub.backend.common.exception.auth;

import com.shulehub.backend.common.exception.ApplicationException;

public abstract class AuthException extends ApplicationException {
    private final String email; // <--- Nuovo campo

    public AuthException(String message, String errorCode, String email) {
        super(message, errorCode);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}