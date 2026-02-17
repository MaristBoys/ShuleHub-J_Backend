package com.shulehub.backend.auth.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ref_profile", schema = "public")
@Data
public class Profile {
    @Id
    private Short id;

    @Column(name = "profile_name")
    private String profileName;

    @Column(name = "profile_des")
    private String profileDescription;
}