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
                // 1. IMPORTANTE: Permetti tutte le richieste OPTIONS (Pre-flight)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/auth/wakeup").permitAll() 
                .requestMatchers("/api/auth/google-login").permitAll()
                .anyRequest().authenticated()
            );

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
            "https://shule-hub.vercel.app/"  // dominio di produzione (Vercel)
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
