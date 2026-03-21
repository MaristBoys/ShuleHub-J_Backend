package com.shulehub.backend.registry.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;




@Entity
@Table(name = "persons", schema = "public")
@Data
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

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


    // --- CAMPI PER IDENTIFICAZIONE ---

    @Column(name = "tin")
    private String tin;

    @Column(name = "national_id")
    private String nationalId;

    /**
     * Colonna generata dal DB (solo numeri del TIN) Tax Identification Number (Tanzania)
     */
    @Column(name = "tin_normalized", insertable = false, updatable = false)
    private String tinNormalized;

    /**
     * Colonna generata dal DB (solo numeri del National ID) national_id: NIDA / NIN (National Identification Number)
     */
    @Column(name = "national_id_normalized", insertable = false, updatable = false)
    private String nationalIdNormalized;

    // --- STATO E RUOLI ---

    @Column(name = "is_visible", nullable = false)
    private boolean isVisible = true;
    
    @Column(name = "is_employee", nullable = false)
    private boolean isEmployee = false;

    @Column(name = "is_student", nullable = false)
    private boolean isStudent = false;

    @Column(name = "is_parent", nullable = false)
    private boolean isParent = false;



    // --- AUDIT E CHANGELOG ---

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // Ho aggiunto i metodi @PrePersist e @PreUpdate per gestire le date in Java,
    // anche se hai un trigger sul database. Questo garantisce che l'oggetto Java sia sincronizzato con lo stato del DB subito dopo il salvataggio.
    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    /**
     * Mapping per la colonna JSONB. 
     * In una configurazione base viene trattata come Stringa (JSON), 
     * ma può essere mappata con Jackson se necessario.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "changelog", columnDefinition = "jsonb")
    private String changelog = "[]";
}