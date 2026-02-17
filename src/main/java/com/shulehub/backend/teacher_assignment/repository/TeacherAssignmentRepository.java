package com.shulehub.backend.teacher_assignment.repository;

import com.shulehub.backend.teacher_assignment.model.entity.TeacherAssignment;
import com.shulehub.backend.teacher_assignment.model.dto.TeacherAssignmentDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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
           "s.subjectNameKsw, " +  // Campo Swahili
           "s.subjectNameEng, " +  // Campo Inglese
           "s.subjectAbbr, " +     // Abbreviazione
           "s.subjectDescription, " + // Descrizione
           "ta.isClassTeacher) " +
           "FROM TeacherAssignment ta " +
           "JOIN YearRoom yr ON ta.yearRoomId = yr.id " +
           "JOIN Room r ON yr.idRoom = r.id " +
           "JOIN Subject s ON ta.idSubject = s.id " +
           "WHERE ta.employee.id = :employeeId " +
           "AND yr.idYear = :activeYearId")
           
    List<TeacherAssignmentDTO> findTeacherContext(
            @Param("employeeId") UUID employeeId, 
            @Param("activeYearId") Short activeYearId
    );
}


