package com.shulehub.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;
import java.time.OffsetDateTime;

@Entity
@Table(name = "users", schema = "public")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String username;
    
    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "google_id", unique = true)
    private String googleId;

    @Column(name = "user_is_active")
    private boolean userIsActive = true;

    @Column(name = "id_profile", nullable = false)
    private Short idProfile;

    @Column(name = "google_name")
    private String googleName;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
}