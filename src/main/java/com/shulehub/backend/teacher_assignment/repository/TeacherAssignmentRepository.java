package com.shulehub.backend.teacher_assignment.repository;

import com.shulehub.backend.teacher_assignment.model.entity.TeacherAssignment;
import com.shulehub.backend.teacher_assignment.model.dto.TeacherAssignmentDTO;
import com.shulehub.backend.school_config.model.entity.YearRoom; // Aggiunto
import com.shulehub.backend.subject.model.entity.Subject;      // Aggiunto

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
}


