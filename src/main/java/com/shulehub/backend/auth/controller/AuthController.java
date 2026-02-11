
//  riceve la chiamata fetch dal login.js.

package com.shulehub.backend.auth.controller;

import com.shulehub.backend.auth.model.dto.UserAuthDTO;
import com.shulehub.backend.auth.service.AuthService;
import com.shulehub.backend.auth.utils.JwtUtils;
import com.shulehub.backend.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
// Nota: in produzione sostituisci "*" con l'URL del tuo frontend
@CrossOrigin(origins = "*", allowCredentials = "true") 
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;

    public AuthController(AuthService authService, JwtUtils jwtUtils) {
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/wakeup")
    public ResponseEntity<String> wakeup() {
        return ResponseEntity.ok("Backend is awake and running!");
    }

    @PostMapping("/google-login")
    public ResponseEntity<ApiResponse<?>> googleLogin(
            @RequestBody Map<String, String> payload,
            HttpServletResponse response) {
        
        String email = payload.get("email");
        
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, "Email mancante", null));
        }

        // Recupero i dati dal service
        UserAuthDTO authData = authService.loginWithGoogle(email);

        // 1. Genero il JWT
        String jwt = jwtUtils.generateToken(email);

        // 2. Creo il Cookie HttpOnly
        ResponseCookie cookie = ResponseCookie.from("shulehub_token", jwt)
                .httpOnly(true)
                .secure(true) // Render usa HTTPS, quindi va bene
                .path("/")
                .maxAge(86400)
                .sameSite("Strict")
                .build();

        // 3. Aggiungo il cookie alla risposta
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // 4. Rispondo con i dati per la UI
        return ResponseEntity.ok(new ApiResponse<>(true, "Login effettuato", authData));
    }
}