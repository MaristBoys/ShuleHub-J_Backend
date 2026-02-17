package com.shulehub.backend.auth.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "ref_permission")
@Data
public class RefPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @Column(name = "permission_code", nullable = false)
    private String permissionCode;

    @Column(name = "permission_des")
    private String permissionDescription;
}