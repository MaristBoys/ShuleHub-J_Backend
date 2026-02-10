package com.shulehub.backend.auth.repository;

import com.shulehub.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    // Questo ci serve per verificare se l'email che arriva da Google esiste già
    Optional<User> findByEmail(String email);
}