
//  riceve la chiamata fetch dal login.js.

package com.shulehub.backend.controller;

import com.shulehub.backend.entity.User;
import com.shulehub.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth") // Modificato da /api/login a /api/auth per allinearsi al SecurityConfig
public class AuthController {

    @Autowired
    private AuthService authService;

    // Endpoint di test per verificare se il server è raggiungibile e pubblico
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(Map.of("message", "Il backend Java è online e funzionante!"));
    }

    @PostMapping("/google")
    public ResponseEntity<?> authenticate(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            User user = authService.verifyGoogleToken(token);
            // Qui potresti voler restituire anche un JWT generato da te, 
            // ma per ora restituiamo l'utente per confermare che funziona.
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }
}