package com.shulehub.backend.auth.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.OffsetDateTime;

@Entity
@Table(name = "ref_permission", schema = "public")
@Data 
@NoArgsConstructor  
@AllArgsConstructor
public class RefPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @Column(name = "permission_code", nullable = false, length = 100, unique = true)
    private String permissionCode;

    // Nel DB hai sia 'description' (text) che 'permission_des' (varchar). 
    // Mappiamo 'permission_des' come descrizione breve
    @Column(name = "permission_des", length = 255)
    private String permissionDescription;

    // Se vuoi mappare anche il campo 'description' di tipo TEXT:
    @Column(name = "description", columnDefinition = "TEXT")
    private String longDescription;

    @Column(name = "permission_is_active", nullable = false)
    private boolean permissionIsActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}