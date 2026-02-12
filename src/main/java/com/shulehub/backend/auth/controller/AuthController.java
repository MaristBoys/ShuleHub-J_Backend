//  riceve le chiamate dal frontend

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
    
        // 1. Leggiamo idToken (come inviato dal frontend)
        String idTokenString = payload.get("idToken");
    
        if (idTokenString == null || idTokenString.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, "idToken mancante", null));
        }

        try {
            // 2. Chiamiamo la verifica che hai già nel tuo AuthServiceImpl
            authService.verifyGoogleToken(idTokenString);

            // 3. Estraiamo l'email dal token (necessaria per il tuo loginWithGoogle)
            // Usiamo la stessa logica che hai nel service per estrarre il payload
            String payloadBase64 = idTokenString.split("\\.")[1];
            String jsonPayload = new String(java.util.Base64.getDecoder().decode(payloadBase64));
            // Estrazione brutale ma efficace dell'email dal JSON
            String email = jsonPayload.split("\"email\":\"")[1].split("\"")[0];

            // 4. Recupero i dati completi (UserAuthDTO) tramite il tuo service
            UserAuthDTO authData = authService.loginWithGoogle(email);

            // 5. Genero il JWT interno
            String jwt = jwtUtils.generateToken(email);

            // 6. Creo il Cookie (Usa "None" se frontend e backend sono su domini diversi)
            ResponseCookie cookie = ResponseCookie.from("shulehub_token", jwt)
                    .httpOnly(true)
                    .secure(true) 
                    .path("/")
                    .maxAge(86400)
                    .sameSite("None") // Fondamentale per Render + Frontend esterno
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            return ResponseEntity.ok(new ApiResponse<>(true, "Login effettuato", authData));

        } catch (Exception e) {
            return ResponseEntity.status(401)
                .body(new ApiResponse<>(false, "Autenticazione fallita: " + e.getMessage(), null));
        }
    }
}