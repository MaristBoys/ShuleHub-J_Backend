package com.shulehub.backend.common.exception.auth;

public class MissingTokenException extends AuthException {
    public MissingTokenException(String message) {
        super(message, "ERR_MISSING_TOKEN");
    }
}