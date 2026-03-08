package com.shulehub.backend.school_config.model.entity;

import com.shulehub.backend.registry.model.entity.Student; // Assicurati che esista nel modulo registry

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(
    name = "cfg_yearroom_student", 
    schema = "public",
    uniqueConstraints = {
        @UniqueConstraint(name = "cfg_yearroom_student_id_yearroom_id_student_key", columnNames = {"id_yearroom", "id_student"}),
        @UniqueConstraint(name = "unique_student_per_year", columnNames = {"id_student", "id_year"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class YearRoomStudent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_yearroom", nullable = false)
    private YearRoom yearRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_student", referencedColumnName = "id_person", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_year", nullable = false)
    private Year year;
}