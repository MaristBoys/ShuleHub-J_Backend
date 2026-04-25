package com.shulehub.backend.school_config.model.entity;

import com.shulehub.backend.registry.model.entity.Student; // Assicurati che esista nel modulo registry
import com.shulehub.backend.school_structure.model.entity.Year;
import com.shulehub.backend.school_structure.model.entity.YearRoom;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

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

    // --- NUOVI CAMPI AGGIUNTI PER ALLINEAMENTO DB ---

    @Column(name = "candidate_number")
    private String candidateNumber;

    /**
     * Corrisponde alla colonna GENERATED ALWAYS as (regexp_replace(...)) STORED.
     * insertable = false, updatable = false: Impedisce a JPA di provare a scrivere il valore (lo fa il DB).
     * @Generated: Istruisce Hibernate a rileggere il valore generato dal database dopo Insert o Update.
     */
    @Column(name = "candidate_number_normalized", insertable = false, updatable = false)
    @Generated(event = {EventType.INSERT, EventType.UPDATE})
    private String candidateNumberNormalized;
}