package com.shulehub.backend.auth.model.entity; 

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "rel_profile_permission")
@Data
public class RelProfilePermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_profile")
    private Short idProfile;

    @Column(name = "id_permission")
    private Short idPermission;
}
