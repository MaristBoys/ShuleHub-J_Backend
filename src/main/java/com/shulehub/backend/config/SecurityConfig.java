

// Dato che hai aggiunto spring-boot-starter-security,
// Spring bloccherà tutte le chiamate di default.
// Devi creare una piccola classe di configurazione per "aprire" l'endpoint del login.

package com.shulehub.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/login/**").permitAll() // Permette il login
                .anyRequest().authenticated() // Protegge tutto il resto
            );
        return http.build();
    }
}