package com.shulehub.backend.registry.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ref_gender", schema = "public")
@Data
public class Gender {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @Column(name = "gender_name", nullable = false, unique = true)
    private String genderName;

    @Column(name = "gender_abbr", nullable = false, unique = true, length = 1)
    private String genderAbbr;
}
