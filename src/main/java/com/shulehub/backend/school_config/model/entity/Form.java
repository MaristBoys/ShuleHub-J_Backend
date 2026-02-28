package com.shulehub.backend.school_config.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "ref_form", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Form {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @Column(name = "form_name", nullable = false, length = 100, unique = true)
    private String formName;

    @Column(name = "form_num", nullable = false, unique = true)
    private Short formNum;

    @Column(name = "form_is_active", nullable = false)
    private boolean formIsActive = true;

    @Column(name = "\"Level\"") // Usiamo le virgolette perché Level è parola riservata in SQL
    private String level;
}