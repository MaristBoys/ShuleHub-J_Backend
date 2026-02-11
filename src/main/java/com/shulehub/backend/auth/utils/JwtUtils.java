package com.shulehub.backend.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {

    @Value("${JWT_SECRET_ENV}")
    private String jwtSecret;

    // 24 ore
    private static final long JWT_EXPIRATION_MS = 24 * 60 * 60 * 1000;

    private SecretKey key;

    @PostConstruct
    public void init() {
        try {

            if (jwtSecret == null || jwtSecret.isBlank()) {
                throw new IllegalStateException("JWT_SECRET_ENV non configurata");
            }

            // Decodifica HEX -> byte reali
            byte[] keyBytes = Hex.decodeHex(jwtSecret.trim());

            if (keyBytes.length < 32) {
                throw new IllegalStateException("JWT secret troppo corta (min 32 byte)");
            }

            this.key = Keys.hmacShaKeyFor(keyBytes);

        } catch (Exception e) {
            throw new IllegalStateException("Errore inizializzazione JWT key", e);
        }
    }

    // =========================
    // GENERAZIONE TOKEN
    // =========================

    public String generateToken(String subject, Map<String, Object> claims) {

        Instant now = Instant.now();

        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(JWT_EXPIRATION_MS)))
                .signWith(key)
                .compact();
    }

    public String generateToken(String subject) {
        return generateToken(subject, Map.of());
    }

    // =========================
    // VALIDAZIONE TOKEN
    // =========================

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    // =========================
    // ESTRAZIONE CLAIMS
    // =========================

    public String extractSubject(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}