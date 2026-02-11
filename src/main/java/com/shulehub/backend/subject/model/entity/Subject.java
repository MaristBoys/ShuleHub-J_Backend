package com.shulehub.backend.subject.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ref_subject", schema = "public")
@Data
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    // Mappatura per il nome in Swahili
    @Column(name = "subject_name_ksw", nullable = false, length = 100, unique = true)
    private String subjectNameKsw;

    // Mappatura per il nome in Inglese
    @Column(name = "subject_name_eng", nullable = false, length = 100, unique = true)
    private String subjectNameEng;

    // Mappatura per l'abbreviazione (obbligatoria nel DB)
    @Column(name = "subject_abbr", nullable = false, length = 10, unique = true)
    private String subjectAbbr;

    // Mappatura per lo stato attivo
    @Column(name = "subject_is_active", nullable = false)
    private boolean subjectIsActive = true;

    // Mappatura per la descrizione (rinominata da subject_des)
    @Column(name = "subject_des", length = 255)
    private String subjectDescription;
}