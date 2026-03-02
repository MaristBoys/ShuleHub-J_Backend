package com.shulehub.backend.common.exception.auth;

import com.shulehub.backend.common.exception.ApplicationException;

public abstract class AuthException extends ApplicationException {
    public AuthException(String message, String errorCode) {
        super(message, errorCode);
    }
}