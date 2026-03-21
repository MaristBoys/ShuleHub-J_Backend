package com.shulehub.backend.registry.model.entity;

import com.shulehub.backend.subject.model.entity.Subject;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "rel_teacher_subject", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_employee", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_subject", nullable = false)
    private Subject subject;
}
