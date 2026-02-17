package com.shulehub.backend.auth.repository;

import com.shulehub.backend.auth.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Recupera un utente tramite email (case-insensitive).
     * Utile per il processo di Login con Google.
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Recupera un utente attivo tramite email.
     * Metodo più sicuro per l'autenticazione.
     */
    Optional<User> findByEmailIgnoreCaseAndUserIsActiveTrue(String email);

    /**
     * Recupera tutti gli utenti appartenenti a un determinato profilo (es. "TEACHER").
     * Sfrutta il mapping ManyToOne con l'entità Profile.
     */
    List<User> findByProfileProfileName(String profileName);

    /**
     * Verifica se esiste già un utente con una determinata email.
     */
    boolean existsByEmailIgnoreCase(String email);
}