

// Dato che hai aggiunto spring-boot-starter-security,
// Spring bloccherà tutte le chiamate di default.
// Devi creare una piccola classe di configurazione per "aprire" l'endpoint del login.

package com.shulehub.backend.common.config;

import com.shulehub.backend.auth.utils.JwtAuthenticationFilter;
import com.shulehub.backend.auth.utils.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtils jwtUtils;

    // Costruttore per iniettare JwtUtils necessario al filtro
    public SecurityConfig(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults()) 
            .csrf(csrf -> csrf.disable())
            // Impostiamo la gestione della sessione come STATELESS (fondamentale per JWT)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Endpoint pubblici (Wakeup e Login)
                .requestMatchers("/api/auth/wakeup", "/api/auth/google-login").permitAll() 
                // Proteggiamo tutto il resto
                .anyRequest().authenticated() 
            );
        
        // Aggiungiamo il nostro filtro custom per i Cookie/JWT prima del filtro di autenticazione standard
        http.addFilterBefore(new JwtAuthenticationFilter(jwtUtils), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Origins permesse (Localhost e GitHub Pages)
        configuration.setAllowedOrigins(List.of(
            "http://localhost:5500", 
            "http://127.0.0.1:5500", 
            "https://maristboys.github.io"
        )); 
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Aggiungiamo i vari header necessari
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        
        // Molto importante: permette l'invio e la ricezione dei Cookie (HttpOnly)
        configuration.setAllowCredentials(true);
        
        // Esponiamo l'header Set-Cookie se necessario (opzionale ma consigliato)
        configuration.setExposedHeaders(List.of("Set-Cookie"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}


