package com.shulehub.backend.registry.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "students", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @Column(name = "id_person")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id_person")
    private Person person;

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_dropped", nullable = false)
    private boolean isDropped = false;

    @Column(name = "dropped_date")
    private LocalDate droppedDate;

    @Column(name = "student_is_active", nullable = false)
    private boolean studentIsActive = true;

    @Column(name = "note", columnDefinition = "text")
    private String note;

    // --- NUOVI CAMPI AGGIUNTI ---

    @Column(name = "prem_number")
    private String premNumber;

    /**
     * Campo GENERATED ALWAYS nel DB. 
     * Usiamo insertable = false e updatable = false perché il valore è gestito da PostgreSQL.
     * @Generated indica a Hibernate di rileggere il valore dal DB dopo ogni insert/update.
     */
    @Column(name = "prem_number_normalized", insertable = false, updatable = false)
    @Generated(event = {EventType.INSERT, EventType.UPDATE})
    private String premNumberNormalized;

    // ----------------------------


    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        // Sebbene il DB abbia un default, è bene settarlo per averlo disponibile subito nell'oggetto
        if (createdAt == null) createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
