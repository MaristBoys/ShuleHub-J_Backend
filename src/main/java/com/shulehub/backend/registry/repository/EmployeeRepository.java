package com.shulehub.backend.registry.repository;

import com.shulehub.backend.registry.model.entity.Employee;
import com.shulehub.backend.teacher_assignment.model.dto.ClassTeacherSelectionDTO;
import com.shulehub.backend.teacher_assignment.model.dto.SubjectTeacherSelectionDTO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    // Questa query serve al tuo AuthService per trovare l'impiegato 
    // partendo dall'ID dello User che ha appena fatto login.
    @Query("SELECT e FROM Employee e WHERE e.id = :personId")
    Optional<Employee> findByPersonId(@Param("personId") UUID personId);


    // Conteggio per la card dashboard
    long countByEmployeeIsActiveTrue();
    // Conta chi è stato assunto tra il 01-01 e il 31-12 dell'anno scelto
    long countByHireDateBetween(LocalDate start, LocalDate end);
    // Conta chi ha terminato il rapporto tra il 01-01 e il 31-12 dell'anno scelto
    long countByEmploymentEndDateBetween(LocalDate start, LocalDate end);



    /**
     * Recupera tutti i docenti attivi per la selezione del Class Teacher.
     * Ordina per nome completo per facilitare la ricerca nel modale.
     */
    @Query("SELECT new com.shulehub.backend.teacher_assignment.model.dto.ClassTeacherSelectionDTO(" +
           "e.id, e.person.fullName) " +
           "FROM Employee e " +
           "WHERE e.employeeIsActive = true " +
           "ORDER BY e.person.fullName ASC")
    List<ClassTeacherSelectionDTO> findAllActiveForClassTeacherSelection();

    /**
     * Recupera i docenti idonei per una specifica materia.
     * Filtra gli impiegati attivi che hanno la materia nel loro profilo (EmployeeSubject).
     */
    @Query("SELECT new com.shulehub.backend.teacher_assignment.model.dto.SubjectTeacherSelectionDTO(" +
           "e.id, e.person.fullName) " +
           "FROM Employee e " +
           "JOIN EmployeeSubject es ON es.employee.id = e.id " +
           "WHERE e.employeeIsActive = true " +
           "AND es.subject.id = :subjectId " +
           "ORDER BY e.person.fullName ASC")
    List<SubjectTeacherSelectionDTO> findActiveBySubjectId(@Param("subjectId") Short subjectId);





}

