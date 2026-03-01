package com.shulehub.backend.common.exception;

// --- Eccezione personalizzata per utenti disabilitati (User Disabled Exception) ---
public class UserDisabledException extends UnauthorizedException {
    public UserDisabledException(String message) {
        super(message);
    }
}