package com.shulehub.backend.auth.model.entity; 

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.OffsetDateTime;

@Entity
@Table(name = "rel_profile_permission", schema = "public")
@Data 
@NoArgsConstructor  
@AllArgsConstructor 
public class RelProfilePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Nel DB hai usato BIGINT, quindi in Java è corretto usare Long (non Integer)
    private Long id;

    @Column(name = "id_profile", nullable = false)
    private Short idProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_permission", referencedColumnName = "id", nullable = false)
    private RefPermission permission;

    // Campo booleano per l'attivazione specifica del profilo
    @Column(name = "profile_permission_is_active", nullable = false)
    private boolean profilePermissionIsActive = true;

    // Campi di Audit (gestiti dal trigger lato DB, ma mappati qui per lettura)
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}