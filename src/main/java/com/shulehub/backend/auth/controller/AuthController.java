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

import com.shulehub.backend.audit.service.ActivityLogService; // 1. Import del servizio per il logging
import jakarta.servlet.http.HttpServletRequest; // Import necessario per IP e Browser che serve le informazioni per il logging

import com.shulehub.backend.common.exception.UnauthorizedException; // Per gestire il catch specifico
import com.fasterxml.jackson.databind.ObjectMapper; // Per il parsing dell'email
import com.fasterxml.jackson.databind.JsonNode;     // Per il parsing dell'email


import java.util.Map;

@RestController
@RequestMapping("/api/auth")
// Nota: in produzione sostituisci "*" con l'URL del tuo frontend
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;
    private final ActivityLogService auditService; // 2. Dichiarazione servizio audit

    public AuthController(AuthService authService, JwtUtils jwtUtils, ActivityLogService auditService) {
        this.authService = authService;
        this.jwtUtils = jwtUtils;
        this.auditService = auditService;
    }

    @GetMapping("/wakeup")
    public ResponseEntity<String> wakeup() {
        return ResponseEntity.ok("Backend is awake and running!");
    }

    @PostMapping("/google-login")
    public ResponseEntity<ApiResponse<?>> googleLogin(
        @RequestBody Map<String, String> payload,
        HttpServletResponse response,
        HttpServletRequest request) { // 4. Aggiunto HttpServletRequest

        // 1. Leggiamo idToken (come inviato dal frontend)
        String idTokenString = payload.get("token");
        
        // 1. Dichiarata fuori dal try per essere visibile nei catch
        String email = "unknown"; // Default per il log in caso di errore prima del parsing

        if (idTokenString == null || idTokenString.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, "idToken mancante", null));
        }

        try {
            // 2. Chiamiamo la verifica che hai già nel tuo AuthServiceImpl
            //System.out.println("Tentativo di login con token: " + idTokenString.substring(0, 10) + "...");
            authService.verifyGoogleToken(idTokenString);
            //System.out.println("Verifica Google superata!");


            // 3. Estraiamo l'email dal token (necessaria per il tuo loginWithGoogle)
            String payloadBase64 = idTokenString.split("\\.")[1];
            byte[] decodedBytes = java.util.Base64.getUrlDecoder().decode(payloadBase64);
            JsonNode node = new ObjectMapper().readTree(decodedBytes);
            
            email = node.get("email").asText();
            String googlePicture = node.has("picture") ? node.get("picture").asText() : null;
            String googleName = node.has("name") ? node.get("name").asText() : null;


            System.out.println("Email estratta dal token: [" + email + "]");

            // 4. Recupero i dati completi (UserAuthDTO) tramite il tuo service
            UserAuthDTO authData = authService.loginWithGoogle(email, googlePicture,googleName);
            System.out.println("Utente trovato nel DB: " + authData.getUsername());

            
            // 5. LOG DI SUCCESSO
            auditService.log(email, authData.getUserId(), "AUTH_LOGIN_SUCCESS", 
                            "Login Google completato con successo", request, null);
            
            // 5.1 Genero il JWT interno
            String jwt = jwtUtils.generateToken(email);

            // 5.2 Creo il Cookie (Usa "None" se frontend e backend sono su domini diversi)
            ResponseCookie cookie = ResponseCookie.from("shulehub_token", jwt)
                    .httpOnly(true)
                    .secure(true) 
                    .path("/")
                    .maxAge(86400)
                    .sameSite("None") // Fondamentale per Render + Frontend esterno
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            return ResponseEntity.ok(new ApiResponse<>(true, "Login effettuato", authData));

        } catch (UnauthorizedException e) {
            // 6. LOG DI ACCESSO NEGATO (Es. Utente non presente nel database ShuleHub)
            auditService.log(email, null, "AUTH_LOGIN_UNAUTHORIZED", 
                            "Tentativo di accesso fallito: " + e.getMessage(), request, null);
            
            return ResponseEntity.status(401)
                    .body(new ApiResponse<>(false, e.getMessage(), null));

        } catch (Exception e) {
            // 7. LOG DI ERRORE DI SISTEMA (Es. Database offline)
            auditService.log(email, null, "AUTH_SYSTEM_ERROR", 
                            "Errore tecnico durante il login: " + e.getMessage(), request, null);
            
            e.printStackTrace(); 
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>(false, "Errore tecnico del server", null));
        }
    }



    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(
            HttpServletRequest request, 
            HttpServletResponse response) {
        
        String email = "unknown";
        
        // 1. Cerchiamo di recuperare l'email dal JWT prima di cancellarlo per il log
        try {
            // Cerchiamo il cookie shulehub_token
            if (request.getCookies() != null) {
                for (var cookie : request.getCookies()) {
                    if ("shulehub_token".equals(cookie.getName())) {
                        String token = cookie.getValue();
                        email = jwtUtils.getEmailFromToken(token); 
                        break;
                    }
                }
            }
        } catch (Exception e) {
            // Se fallisce il recupero email, logghiamo come unknown
        }

        // 2. Scriviamo il log di Logout
        auditService.log(email, null, "AUTH_LOGOUT", "Logout effettuato dall'utente", request, null);

        // 3. Creiamo un cookie "vuoto" con scadenza immediata per cancellarlo dal browser
        ResponseCookie deleteCookie = ResponseCookie.from("shulehub_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0) // Scade ora, quindi viene rimosso
                .sameSite("None")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());

        return ResponseEntity.ok(new ApiResponse<>(true, "Logout effettuato con successo", null));
    }

}


