
//  riceve la chiamata fetch dal login.js.

package com.shulehub.backend.controller;

import com.shulehub.backend.entity.User;
import com.shulehub.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/login")
@CrossOrigin(origins = "*") // In produzione metterai l'URL di GitHub Pages
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/google")
    public ResponseEntity<?> authenticate(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            User user = authService.verifyGoogleToken(token);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }
}
