package com.shulehub.backend.teacher_assignment.service;

import com.shulehub.backend.registry.model.entity.Employee;
import com.shulehub.backend.registry.repository.EmployeeRepository;
import com.shulehub.backend.school_structure.model.entity.YearRoom;
import com.shulehub.backend.school_structure.repository.YearRoomRepository;
import com.shulehub.backend.subject.model.entity.Subject;
import com.shulehub.backend.subject.repository.SubjectRepository;
import com.shulehub.backend.teacher_assignment.model.dto.ClassTeacherSelectionDTO;
import com.shulehub.backend.teacher_assignment.model.dto.SubjectTeacherSelectionDTO;
import com.shulehub.backend.teacher_assignment.model.entity.TeacherAssignment;
import com.shulehub.backend.teacher_assignment.repository.TeacherAssignmentRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeacherAssignmentService {

    private final TeacherAssignmentRepository assignmentRepository;
    private final EmployeeRepository employeeRepository;
    private final YearRoomRepository yearRoomRepository;
    private final SubjectRepository subjectRepository;

    /**
     * Recupera la lista di docenti idonei per il ruolo di Class Teacher.
     */
    @Transactional(readOnly = true)
    public List<ClassTeacherSelectionDTO> getEligibleClassTeachers() {
        return employeeRepository.findAllActiveForClassTeacherSelection();
    }

    /**
     * Recupera la lista di docenti idonei per insegnare una specifica materia.
     */
    @Transactional(readOnly = true)
    public List<SubjectTeacherSelectionDTO> getEligibleTeachersForSubject(Short subjectId) {
        return employeeRepository.findActiveBySubjectId(subjectId);
    }

    /**
     * Assegna o aggiorna il Class Teacher di una stanza.
     * prevede anche il caso di svuotarlo
     * Logica: Cerca se esiste già un record con subject=null, se sì lo aggiorna, altrimenti lo crea.
     */
    @Transactional
    public void assignClassTeacher(Integer yearRoomId, UUID employeeId) {
        // 1. Cerchiamo se esiste già un'assegnazione Class Teacher per questa stanza
        // Usiamo il metodo già presente nel repository
        Optional<TeacherAssignment> existingAssignment = assignmentRepository
                .findByYearRoomIdAndSubjectIsNullAndClassTeacherTrue(yearRoomId);

        // 2. CASO RIMOZIONE: Se l'ID dell'impiegato è null, procediamo al delete
        if (employeeId == null) {
            existingAssignment.ifPresent(assignment -> {
                // Utilizziamo il metodo delete predefinito di JpaRepository
                assignmentRepository.delete(assignment);
            });
            return; 
        }

        // 3. CASO ASSEGNAZIONE O UPDATE:
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));
        
        // Se l'assegnazione esiste già, la aggiorniamo, altrimenti ne creiamo una nuova
        TeacherAssignment assignment = existingAssignment.orElseGet(() -> {
            YearRoom yearRoom = yearRoomRepository.findById(yearRoomId)
                    .orElseThrow(() -> new RuntimeException("YearRoom not found with ID: " + yearRoomId));
            
            TeacherAssignment newAssignment = new TeacherAssignment();
            newAssignment.setYearRoom(yearRoom);
            newAssignment.setSubject(null);
            newAssignment.setClassTeacher(true);
            return newAssignment;
        });

        assignment.setEmployee(employee);
        
        // Il save gestisce sia l'inserimento del nuovo che l'update dell'esistente
        assignmentRepository.save(assignment);
    }

    /**
     * Assegna un docente a una specifica materia (Staffing).
     */
    @Transactional
    public void assignSubjectTeacher(Integer yearRoomId, Short subjectId, UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        YearRoom yearRoom = yearRoomRepository.findById(yearRoomId)
                .orElseThrow(() -> new RuntimeException("YearRoom not found"));
                
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        // Cerchiamo se esiste già un'assegnazione per questa materia in questa stanza
        TeacherAssignment assignment = assignmentRepository
                .findByYearRoomIdAndSubjectId(yearRoomId, subjectId)
                .orElse(new TeacherAssignment());

        if (assignment.getId() == null) {
            assignment.setYearRoom(yearRoom);
            assignment.setSubject(subject);
            assignment.setClassTeacher(false);
        }

        assignment.setEmployee(employee);
        assignmentRepository.save(assignment);
    }
}
