package com.shulehub.backend.subject.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ref_subject")
@Data
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @Column(name = "subject_name", nullable = false)
    private String subjectName;

    @Column(name = "subject_des")
    private String subjectDescription;
}
