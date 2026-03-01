/**
 * Gestore centralizzato delle eccezioni (Global Exception Handler).
 * * OBIETTIVO:
 * Intercettare tutte le eccezioni sollevate dai vari moduli del backend (Auth, TeacherAssignment, ecc.)
 * e trasformarle in risposte JSON standardizzate (ApiResponse). Questo evita che il frontend 
 * riceva errori HTML generici o stacktrace tecnici, garantendo un'esperienza utente fluida.
 * * FUNZIONAMENTO:
 * Grazie all'annotazione @RestControllerAdvice, Spring Boot monitora tutti i Controller.
 * Quando un metodo lancia un'eccezione:
 * 1. Spring cerca un metodo annotato con @ExceptionHandler che gestisca quel tipo di errore.
 * 2. Il metodo cattura l'eccezione e impacchetta il messaggio dentro un oggetto ApiResponse.
 * 3. Restituisce una ResponseEntity con lo status code HTTP appropriato (es. 401 per Unauthorized, 500 per errori generici).
 */

package com.shulehub.backend.common.exception;

import com.shulehub.backend.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Gestione specifica per Utente Non Trovato
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(false, ex.getMessage(), "ERR_USER_NOT_FOUND"));
    }

    // 2. Gestione specifica per Utente Disabilitato
    @ExceptionHandler(UserDisabledException.class)
    public ResponseEntity<ApiResponse<String>> handleUserDisabled(UserDisabledException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(false, ex.getMessage(), "ERR_USER_DISABLED"));
    }

    // 3. Gestione per Token Invalido (altre UnauthorizedException)
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<String>> handleUnauthorized(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(false, ex.getMessage(), "ERR_INVALID_TOKEN"));
    }

    // 4. Gestione Errori Generici di Runtime (500)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Errore interno del server", "ERR_INTERNAL_SERVER"));
    }
}