package com.shulehub.backend.auth.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${shulehub.jwt.secret}")
    private String jwtSecret;

    // Durata del token: 24 ore
    private final long jwtExpirationMs = 86400000; 

    private Key key;

    /**
     * Questo metodo viene eseguito automaticamente dopo che Spring ha iniettato il valore di jwtSecret.
     * Serve a preparare la chiave crittografica in modo sicuro.
     */
    @PostConstruct
    public void init() {
        // .trim() rimuove eventuali spazi bianchi o ritorni a capo invisibili dalla variabile di Render
        String cleanSecret = jwtSecret.trim();
        
        // Trasformiamo la stringa in un oggetto Key utilizzabile dall'algoritmo HS256
        this.key = Keys.hmacShaKeyFor(cleanSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // Loggare l'eccezione potrebbe aiutare in fase di debug, 
            // ma restituiamo false per invalidare il token
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}