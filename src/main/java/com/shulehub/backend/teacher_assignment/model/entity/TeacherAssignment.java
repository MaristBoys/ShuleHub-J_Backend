package com.shulehub.backend.teacher_assignment.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "cfg_yearroom_subject_teacher")
@Data
public class TeacherAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_yearroom")
    private Integer idYearRoom;

    @Column(name = "id_subject")
    private Short idSubject;

    @Column(name = "id_employee")
    private UUID idEmployee;

    @Column(name = "is_class_teacher")
    private boolean isClassTeacher;
}
