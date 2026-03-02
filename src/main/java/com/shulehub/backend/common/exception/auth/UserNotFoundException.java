package com.shulehub.backend.common.exception.auth;

public class UserNotFoundException extends AuthException {
    public UserNotFoundException(String message) {
        super(message, "ERR_USER_NOT_FOUND");
    }
}