package com.shulehub.backend.teacher_assignment.model.entity;

import com.shulehub.backend.registry.model.entity.Employee;
import com.shulehub.backend.school_config.model.entity.YearRoom;
import com.shulehub.backend.subject.model.entity.Subject;     

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
    name = "cfg_yearroom_subject_teacher", 
    schema = "public",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id_yearroom", "id_subject"})
    }
)

@Data
public class TeacherAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // RIMUOVI i campi Integer/Short semplici e usa le relazioni:

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_yearroom", nullable = false)
    private YearRoom yearRoom; // Questo permette "JOIN ta.yearRoom"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_subject")
    private Subject subject;   // Questo permette "JOIN ta.subject"

    @Column(name = "is_class_teacher", nullable = false)
    private boolean classTeacher = false;

    /**
     * Colleghiamo l'impiegato.
     * La colonna nel DB si chiama id_employee, ma punta alla PK id_person della tabella employees.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_employee", referencedColumnName = "id_person")
    private Employee employee;
}