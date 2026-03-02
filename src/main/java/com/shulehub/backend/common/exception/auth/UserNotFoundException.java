package com.shulehub.backend.common.exception.auth;

public class UserNotFoundException extends AuthException {
    public UserNotFoundException(String message, String email) {
        super(message, "ERR_USER_NOT_FOUND", email); // <--- Passa l'email
    }
}