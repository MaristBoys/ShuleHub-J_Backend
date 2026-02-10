package com.shulehub.backend.teacher_assignment.repository;

import com.shulehub.backend.teacher_assignment.model.entity.TeacherAssignment;
import com.shulehub.backend.teacher_assignment.model.dto.TeacherAssignmentDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeacherAssignmentRepository extends JpaRepository<TeacherAssignment, Integer> {

    @Query("SELECT new com.shulehub.backend.teacher_assignment.model.dto.TeacherAssignmentDTO(" +
           "yr.id, r.roomName, s.id, s.subjectName, ta.isClassTeacher) " +
           "FROM TeacherAssignment ta " +
           "JOIN YearRoom yr ON ta.idYearRoom = yr.id " +
           "JOIN Room r ON yr.idRoom = r.id " +
           "JOIN Subject s ON ta.idSubject = s.id " +
           "WHERE ta.idEmployee = :employeeId " +
           "AND yr.idYear = :activeYearId")
    List<TeacherAssignmentDTO> findTeacherContext(
            @Param("employeeId") UUID employeeId, 
            @Param("activeYearId") Short activeYearId
    );
}