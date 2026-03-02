package com.shulehub.backend.common.exception.auth;

public class UserDisabledException extends AuthException {
    public UserDisabledException(String message, String email) {
        super(message, "ERR_USER_DISABLED", email);
    }
}