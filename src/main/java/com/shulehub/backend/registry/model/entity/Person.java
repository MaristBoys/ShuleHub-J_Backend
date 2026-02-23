package com.shulehub.backend.registry.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "persons", schema = "public")
@Data
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "id_user", unique = true)
    private UUID idUser;

    @Column(name = "first_name", length = 100, nullable = false)
    private String firstName;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "last_name", length = 100, nullable = false)
    private String lastName;

    /**
     * Campo generato automaticamente dal database.
     * Usiamo insertable = false e updatable = false per evitare che JPA tenti di scriverlo.
     */
    @Column(name = "full_name", insertable = false, updatable = false)
    private String fullName;

    @Column(name = "id_gender", nullable = false)
    private Short idGender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "id_district")
    private Short idDistrict;

    @Column(name = "homeplace_village", length = 100)
    private String homeplaceVillage;

    @Column(name = "is_employee", nullable = false)
    private boolean isEmployee = false;

    @Column(name = "is_student", nullable = false)
    private boolean isStudent = false;

    @Column(name = "is_parent", nullable = false)
    private boolean isParent = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    /**
     * Mapping per la colonna JSONB. 
     * In una configurazione base viene trattata come Stringa (JSON), 
     * ma può essere mappata con Jackson se necessario.
     */
    @Column(name = "changelog", columnDefinition = "jsonb")
    private String changelog = "[]";
}