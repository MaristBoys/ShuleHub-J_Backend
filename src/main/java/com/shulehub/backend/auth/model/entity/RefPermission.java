package com.shulehub.backend.auth.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "ref_permission")
@Data // Genera Getter, Setter, toString, equals e hashCode
@NoArgsConstructor  // Genera il costruttore vuoto richiesto da JPA (risolve l'import alert)
@AllArgsConstructor // Genera il costruttore con tutti i campi (risolve l'import alert)

public class RefPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @Column(name = "permission_code", nullable = false)
    private String permissionCode;

    @Column(name = "permission_des")
    private String permissionDescription;
}