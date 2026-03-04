package com.shulehub.backend.auth.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collections; 
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Component
public class JwtUtils {

    @Value("${shulehub.jwt.secret}") // Punta alla proprietà nel file .properties
    // @Value("${JWT_SECRET_ENV}") //punta diretto alla variabile d'ambiente    
    private String jwtSecret;

    private static final long JWT_EXPIRATION_MS = 24 * 60 * 60 * 1000;

    private SecretKey key;

    @PostConstruct
    public void init() {
        try {

            if (jwtSecret == null || jwtSecret.isBlank()) {
                throw new IllegalStateException("JWT_SECRET_ENV non configurata");
            }

            byte[] keyBytes = Hex.decodeHex(jwtSecret.trim());

            if (keyBytes.length < 32) {
                throw new IllegalStateException("JWT secret troppo corta");
            }

            this.key = Keys.hmacShaKeyFor(keyBytes);

        } catch (Exception e) {
            throw new IllegalStateException("Errore inizializzazione JWT key", e);
        }
    }

    // =========================
    // GENERAZIONE TOKEN
    // =========================

    Date now = new Date();
    Date expiry = new Date(now.getTime() + JWT_EXPIRATION_MS);

    public String generateToken(String email, UUID userId, String profileName,Set<String> permissions) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + JWT_EXPIRATION_MS);

        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId.toString()) // Inserisci l'ID ricevuto // come stringa nei claim perché JWT lavora con stringhe
                .claim("role", "ROLE_" + profileName.toUpperCase()) // Aggiungi il ruolo come claim
                .claim("permissions", permissions) // Inseriamo la lista dei codici permesso (es. ["EDIT_GRADES", "VIEW_CONFIG"])   
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(this.key)
                .compact();
    }


    // =========================
    // VALIDAZIONE TOKEN
    // =========================

    public boolean validateToken(String token) {
        try {

            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return true;

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // =========================
    // METODI COMPATIBILI COL TUO FILTER
    // =========================

    public Claims extractAllClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getEmailFromToken(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }


    public UUID getUserIdFromToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            String userIdString = claims.get("userId", String.class);
            return userIdString != null ? UUID.fromString(userIdString) : null;
        } catch (Exception e) {
            // Logga l'errore se necessario per il debug
            return null; // Token invalido o claim assente
        }
    }

    public String getRoleFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }

    // Aggiungi il metodo per estrarre i permessi
    public List<String> getPermissionsFromToken(String token) {
        Claims claims = extractAllClaims(token);
        Object permissions = claims.get("permissions");
        
        if (permissions instanceof List<?>) {
            return ((List<?>) permissions).stream()
                    .map(Object::toString)
                    .toList();
        }
        return Collections.emptyList();
    }

}
