package com.shulehub.backend.common.exception;

// --- Eccezione personalizzata per utenti non trovati (User Not Found Exception) ---

public class UserNotFoundException extends UnauthorizedException {
    public UserNotFoundException(String message) {
        super(message);
    }
}