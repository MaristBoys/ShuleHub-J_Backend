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

import org.springframework.http.HttpMethod;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtils jwtUtils;

    public SecurityConfig(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 1. Permetti tutte le richieste OPTIONS (Pre-flight)
                // Questo è importante per il CORS: le richieste pre-flight (OPTIONS) devono essere permesse senza autenticazione
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/auth/wakeup").permitAll() 
                .requestMatchers("/api/auth/google-login").permitAll()
                .requestMatchers("/api/auth/logout").permitAll()
                
                // 2. non utilizziamo i ruoli .hasAnyRole(...) 
                // Permettiamo l'accesso a tutti i loggati (.authenticated())
                // Sarà il Controller con @PreAuthorize a decidere chi può fare cosa.
                .requestMatchers("/api/v1/dashboard/**").authenticated()
                .requestMatchers("/api/v1/school-config/**").authenticated()
                .requestMatchers("/api/v1/subjects/**").authenticated()
                .requestMatchers("/api/v1/school-structure/**").authenticated()
                .requestMatchers("/api/v1/indicator-scales/**").authenticated()

                // 3. Per tutte le altre rotte non specificate, richiediamo autenticazione
                .anyRequest().authenticated()
            );

       
        /* Dato che il tuo sistema prevede dei Permissions (file RefPermission.java), 
        spostiamo il controllo dal "Nome del Profilo" al "Codice del Permesso".
        La gestione dei permessi specifici (come CONFIG_EDIT_YEAR) avviene a livello di Controller,
        usando @PreAuthorize sui singoli endpoint. 
        In questo modo, invece di controllare se l'utente ha un ruolo specifico, controlliamo 
        se ha un permesso specifico. Per fare questo il JWT include i permessi dell'utente
        e il filtro JWT li carica nelle authorities di Spring Security.
        */

        
        http.addFilterBefore(
            new JwtAuthenticationFilter(jwtUtils),
            UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
            "http://localhost:5500",    // live server di VSCode
            "http://127.0.0.1:5500",    // live server di VSCode
            "http://localhost:5173",  // Vite default port per sviluppo
            "http://localhost:4173",  // Vite default port per produzione
            "https://maristboys.github.io",  // dominio di produzione (GitHub Pages)
            "https://shule-hub.vercel.app"  // dominio di produzione (Vercel)
        ));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept","X-Requested-With", "Origin"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Set-Cookie", "Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
