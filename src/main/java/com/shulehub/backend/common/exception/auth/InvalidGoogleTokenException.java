package com.shulehub.backend.common.exception.auth;

public class InvalidGoogleTokenException extends AuthException {
    public InvalidGoogleTokenException(String message, String email) {
        super(message, "ERR_INVALID_TOKEN", email);
    }
}
