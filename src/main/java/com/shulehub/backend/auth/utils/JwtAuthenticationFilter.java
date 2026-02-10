/*Per far sì che il backend riconosca l'utente nelle chiamate successive, dobbiamo aggiungere il Filtro di Sicurezza.
Questo componente intercetta ogni richiesta, apre il "pacchetto" (il cookie), valida il JWT e autentica l'utente nel contesto di Spring Security. */

package com.shulehub.backend.auth.utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.ArrayList;


public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String token = null;

        // 1. Estraiamo il token dal cookie
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("shulehub_token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // 2. Validiamo il token e impostiamo l'autenticazione
        if (token != null && jwtUtils.validateToken(token)) {
            String email = jwtUtils.getEmailFromToken(token);
            
            // Creiamo l'oggetto di autenticazione per Spring
            UsernamePasswordAuthenticationToken auth = 
                new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());
            
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}