package com.shulehub.backend.auth.controller;

import com.shulehub.backend.auth.model.dto.UserAuthDTO;
import com.shulehub.backend.auth.service.AuthService;
import com.shulehub.backend.auth.utils.JwtUtils;
import com.shulehub.backend.common.exception.auth.InvalidGoogleTokenException;
import com.shulehub.backend.common.exception.auth.MissingTokenException;
import com.shulehub.backend.common.response.ApiResponse;
import com.shulehub.backend.audit.service.ActivityLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;
    private final ActivityLogService auditService;

    public AuthController(AuthService authService, JwtUtils jwtUtils, ActivityLogService auditService) {
        this.authService = authService;
        this.jwtUtils = jwtUtils;
        this.auditService = auditService;
    }

    // --- Endpoint per svegliare il backend (utile su hosting come Render) ---
    @GetMapping("/wakeup")
    public ResponseEntity<String> wakeup() {
        return ResponseEntity.ok("Backend is awake and running!");
    }

    // --- Endpoint di login con Google ---
    @PostMapping("/google-login")
    public ResponseEntity<ApiResponse<?>> googleLogin(
            @RequestBody Map<String, String> payload,
            HttpServletResponse response,
            HttpServletRequest request) {

        // 1 Controllo token mancante
        String idTokenString = payload.get("token");
        if (idTokenString == null || idTokenString.isEmpty()) {
            throw new MissingTokenException("Token Google non pervenuto", "unknown");
        }

        // 2 Estrazione email, nome e picture dal token
        String email="unknown";
        String googlePicture = null;
        String googleName = null;
 
        try {
            var node = new com.fasterxml.jackson.databind.ObjectMapper()
                    .readTree(Base64.getUrlDecoder().decode(idTokenString.split("\\.")[1]));

            if (!node.has("email")) {
                throw new InvalidGoogleTokenException("Email mancante nel token", email);
            }

            email = node.get("email").asText();
            googlePicture = node.has("picture") ? node.get("picture").asText() : null;
            googleName = node.has("name") ? node.get("name").asText() : null;
        } catch (Exception e) {
            throw new InvalidGoogleTokenException("Token Google malformato", email);
        }

        // 3 Verifica token con Google
        authService.verifyGoogleToken(idTokenString);

        // 4 Recupero dati utente tramite service
        UserAuthDTO authData = authService.loginWithGoogle(email, googlePicture, googleName);

        // 5 Log di successo
        auditService.log(email, authData.getUserId(), "AUTH_LOGIN_SUCCESS",
                "Login Google completato con successo", request, null);

        // 6 Generazione JWT e cookie
        // generazione del token JWT con email e userId presi da authData, che è un DTO che contiene le informazioni dell'utente recuperate o create durante il login
        String jwt = jwtUtils.generateToken(authData.getEmail(), authData.getUserId(),authData.getProfileName(),authData.getPermissions());
        // generazione del cookie HTTP-only con il token JWT, che sarà inviato al client e usato per autenticare le richieste future
        ResponseCookie cookie = ResponseCookie.from("shulehub_token", jwt)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(86400)
                .sameSite("None")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        //  Risposta al frontend
        return ResponseEntity.ok(new ApiResponse<>(true, "Login effettuato", authData));
    }

    // --- Endpoint di logout ---
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(
            HttpServletRequest request,
            HttpServletResponse response) {

        String email = "unknown";
        UUID userId = null;

        // 1 Recupero email e userId dal cookie se presente
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("shulehub_token".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    email = jwtUtils.getEmailFromToken(token);
                    
                    userId = jwtUtils.getUserIdFromToken(token);
                    break;
                }
            }
        }

        // 2 Log di logout
        auditService.log(email, userId, "AUTH_LOGOUT", "Logout effettuato dall'utente", request, null);

        // 3 Cancellazione cookie
        ResponseCookie deleteCookie = ResponseCookie.from("shulehub_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());

        // 4 Risposta al frontend
        return ResponseEntity.ok(new ApiResponse<>(true, "Logout effettuato con successo", null));
    }
}