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
import com.shulehub.backend.common.exception.UserDisabledException;
import com.shulehub.backend.common.exception.UserNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper; // Per il parsing dell'email
import com.fasterxml.jackson.databind.JsonNode;     // Per il parsing dell'email

import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
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

        // Dichiarata fuori dal try per essere visibile nei catch
        String email = "unknown"; // Default per il log in caso di errore prima del parsing
        String googlePicture = null;
        String googleName = null;


        // 1. Leggiamo idToken (come inviato dal frontend)
        String idTokenString = payload.get("token");
        
        // RISPOSTA CASO 1: Token totalmente mancante (Frontend non lo ha inviato)
        if (idTokenString == null || idTokenString.isEmpty()) {
            auditService.log("unknown", null, "AUTH_ERROR", "Chiamata senza token", request, null);
        
            return ResponseEntity.badRequest() // Metodo rapido per lo stato 400
                .body(new ApiResponse<>(false, "Token Google non pervenuto", "ERR_MISSING_TOKEN"));
        }

        try {
            // 1. Estrazione "grezza" e controllo immediato
            try {
                String payloadBase64 = idTokenString.split("\\.")[1];
                JsonNode node = new ObjectMapper().readTree( Base64.getUrlDecoder().decode(payloadBase64));
                
                if (!node.has("email")) {
                    throw new Exception("Email missing in token"); // Salta al catch interno
                }
                
                email = node.get("email").asText();
                // Possiamo già recuperare anche gli altri dati qui
                googlePicture = node.has("picture") ? node.get("picture").asText() : null;
                googleName = node.has("name") ? node.get("name").asText() : null;

            } catch (Exception e) {
                // Se il token è rotto o manca l'email, logghiamo e usciamo subito
                auditService.log("unknown", null, "AUTH_ERROR", "Token malformato o email mancante", request, null);
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Token non valido o incompleto", "ERR_MALFORMED_TOKEN"));
            }
            
            
            // 2. verifica del token (metodo presente nel AuthServiceImpl)
            authService.verifyGoogleToken(idTokenString);
           
            // 3. Estraiamo l'email dal token (vecchio metodo, ora abbiamo già l'email dal parsing precedente, ma lo lascio per chiarezza)
            //String payloadBase64 = idTokenString.split("\\.")[1];
            //byte[] decodedBytes = java.util.Base64.getUrlDecoder().decode(payloadBase64);
            //JsonNode node = new ObjectMapper().readTree(decodedBytes);
            
            // 4. Recupero i dati completi (UserAuthDTO) tramite il service
            UserAuthDTO authData = authService.loginWithGoogle(email, googlePicture,googleName);
            //System.out.println("Utente trovato nel DB: " + authData.getUsername());

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

        } catch (UserNotFoundException e) {
            auditService.log(email, null, "AUTH_USER_NOT_FOUND", e.getMessage(), request, null);
            throw e;
        } catch (UserDisabledException e) {
            auditService.log(email, null, "AUTH_USER_DISABLED", e.getMessage(), request, null);
            throw e;
        } catch (UnauthorizedException e) {
            auditService.log(email, null, "AUTH_INVALID_TOKEN", e.getMessage(), request, null);
            throw e;
        } catch (Exception e) {
            auditService.log(email, null, "AUTH_SYSTEM_ERROR", e.getMessage(), request, null);
            throw new RuntimeException(e.getMessage(), e);
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


