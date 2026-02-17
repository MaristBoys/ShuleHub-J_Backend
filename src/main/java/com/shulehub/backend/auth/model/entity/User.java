package com.shulehub.backend.auth.model.entity;

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

    // hibernate fa la query per recuperare la classe profile che mappa ref_profile tramite annotazione @ManyToOne(fetch = FetchType.EAGER);
    // EAGER significa che lo recupera fin da subito mentre LAZY quando il dato è richiesto con una seconda query

    @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "id_profile", nullable = false)
    private Profile profile;  //oggetto profile della classe Profile

    @Column(name = "google_name")
    private String googleName;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "note")
    private String note; // Campo correttamente inserito

    // Changelog se dovesse servire in futuro come String o JsonNode
    // private String changelog;
}