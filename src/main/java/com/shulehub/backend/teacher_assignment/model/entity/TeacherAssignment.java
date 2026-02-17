package com.shulehub.backend.teacher_assignment.model.entity;

import com.shulehub.backend.registry.model.entity.Employee;

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
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;

    @Column(name = "id_yearroom", nullable = false)
    private Integer yearRoomId;

    @Column(name = "id_subject")
    private Short subjectId;

    @Column(name = "is_class_teacher", nullable = false)
    private boolean classTeacher = false;

    /**
     * Colleghiamo l'impiegato.
     * La colonna nel DB si chiama id_employee, ma punta alla PK id_person della tabella employees.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "id_employee", 
        referencedColumnName = "id_person",
        foreignKey = @ForeignKey(name = "fk_cyrts_employee") // Anche se non esplicita nello script, la definiamo per JPA
    )
    private Employee employee;
}