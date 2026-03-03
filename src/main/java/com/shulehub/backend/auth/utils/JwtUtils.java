package com.shulehub.backend.auth.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.shulehub.backend.auth.model.entity.User;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
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
/*
    public String generateToken(String email) {
        return generateToken(email, Map.of());
    }
 
    public String generateToken(String email, Map<String, Object> claims) {

        Date now = new Date();
        Date expiry = new Date(now.getTime() + JWT_EXPIRATION_MS);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    public String generateToken(User user) { // Passa l'oggetto User, non solo l'email
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId()) // <--- Aggiungi l'ID qui
                .setIssuedAt(new Date())
                .setExpiration(expiry)
                .signWith(this.key)
                .compact();
    }
*/
    public String generateToken(String email, UUID userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + JWT_EXPIRATION_MS);

        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId.toString()) // Inserisci l'ID ricevuto // come stringa nei claim perché JWT lavora con stringhe
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

    public String getEmailFromToken(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Claims extractAllClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public UUID getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(this.key) // Usa la key inizializzata nel tuo @PostConstruct
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Recuperiamo l'ID dai claims
            String userIdString = claims.get("userId", String.class);
            return userIdString != null ? UUID.fromString(userIdString) : null;
        } catch (Exception e) {
            return null; // Token invalido o claim assente
        }
    }
}
