package com.shulehub.backend.teacher_assignment.repository;

import com.shulehub.backend.teacher_assignment.model.entity.TeacherAssignment;
import com.shulehub.backend.teacher_assignment.model.dto.TeacherAssignmentDTO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

//Sta utilizzando una tecnica chiamata "Constructor Projection"
//Invece di scaricare l'intera entità TeacherAssignment (che contiene solo ID numerici)
//e poi dover fare altre query per i nomi delle classi o delle materie,
//la query crea direttamente il TeacherAssignmentDTO


@Repository
public interface TeacherAssignmentRepository extends JpaRepository<TeacherAssignment, Integer> {

    @Query("SELECT new com.shulehub.backend.teacher_assignment.model.dto.TeacherAssignmentDTO(" +
       "yr.id, " +
       "r.roomName, " +
       "s.id, " +
       "s.subjectNameKsw, " + 
       "s.subjectNameEng, " + 
       "s.subjectAbbr, " + 
       "s.subjectDescription, " + 
       "ta.classTeacher) " + 
       "FROM TeacherAssignment ta " +
       "JOIN ta.yearRoom yr " +
       "JOIN yr.room r " +
       "JOIN ta.subject s " +
       "WHERE ta.employee.id = :employeeId " +
       "AND yr.year.id = :activeYearId")
    List<TeacherAssignmentDTO> findTeacherContext(
            @Param("employeeId") UUID employeeId, 
            @Param("activeYearId") Short activeYearId
    );

    /**
     * Recupera tutti i docenti e le materie assegnate a una specifica YearRoom.
     * Spring JPA navigherà automaticamente la relazione 'yearRoom' e userà l'ID.
     */
    List<TeacherAssignment> findByYearRoomId(Integer yearRoomId);




    /**
     * Recupera l'assegnazione del Class Teacher per una specifica YearRoom.
     * Per convenzione, il Class Teacher ha subject = null e classTeacher = true.
     */
    Optional<TeacherAssignment> findByYearRoomIdAndSubjectIsNullAndClassTeacherTrue(Integer yearRoomId);

    /**
     * Recupera tutte le assegnazioni (Staffing) per una stanza, escluso il Class Teacher.
     * Utile per popolare il tab Staffing nel modale.
     */
    @Query("SELECT ta FROM TeacherAssignment ta " +
           "LEFT JOIN FETCH ta.employee e " +
           "LEFT JOIN FETCH e.person " +
           "LEFT JOIN FETCH ta.subject s " +
           "WHERE ta.yearRoom.id = :yearRoomId " +
           "AND ta.subject IS NOT NULL " +
           "ORDER BY s.subjectNameEng ASC")
    List<TeacherAssignment> findStaffingByYearRoomId(@Param("yearRoomId") Integer yearRoomId);

    /**
     * Trova un'assegnazione specifica per materia.
     * Utile quando dobbiamo aggiornare il docente di una materia già presente.
     */
    Optional<TeacherAssignment> findByYearRoomIdAndSubjectId(Integer yearRoomId, Short subjectId);

    /**
     * Verifica se un docente è già impegnato come Class Teacher in un'altra stanza
     * nello stesso anno scolastico (evita sovrapposizioni).
     */
    boolean existsByEmployeeIdAndYearRoomYearIdAndClassTeacherTrue(UUID employeeId, Short yearId);

}


