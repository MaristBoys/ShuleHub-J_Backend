package com.shulehub.backend.registry.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
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

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
