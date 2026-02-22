package com.shulehub.backend.auth.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

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
}
