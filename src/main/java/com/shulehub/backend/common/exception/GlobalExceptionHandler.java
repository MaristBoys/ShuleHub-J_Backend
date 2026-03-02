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

import com.shulehub.backend.common.exception.auth.AuthException;
import com.shulehub.backend.common.response.ApiResponse;
import com.shulehub.backend.audit.service.ActivityLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ActivityLogService auditService;

    public GlobalExceptionHandler(ActivityLogService auditService) {
        this.auditService = auditService;
    }

    // --- 1 Gestione centralizzata eccezioni di autenticazione (audit + response) ---
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiResponse<String>> handleAuthException(AuthException ex, HttpServletRequest request) {
        // Log attività utente
        auditService.log(
                "unknown", // se l’email non è nota dal token, altrimenti può essere passato
                null,
                ex.getErrorCode(),  // usa l’errorCode dell’eccezione come evento di audit
                ex.getMessage(),
                request,
                null
        );

        // Mappatura HttpStatus basata sul tipo di eccezione
        HttpStatus status = switch (ex.getClass().getSimpleName()) {
            case "UserNotFoundException", "UserDisabledException", "InvalidGoogleTokenException" -> HttpStatus.UNAUTHORIZED;
            case "MissingTokenException" -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.UNAUTHORIZED;
        };

        return ResponseEntity.status(status)
                .body(new ApiResponse<>(false, ex.getMessage(), ex.getErrorCode()));
    }

    // --- 2 Gestione Errori Generici di Runtime (500) ---
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        // Log tecnico (stacktrace) senza audit
        ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Errore interno del server", "ERR_INTERNAL_SERVER"));
    }

}