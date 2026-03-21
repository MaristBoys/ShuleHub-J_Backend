package com.shulehub.backend.teacher_assignment.controller;

import com.shulehub.backend.common.response.ApiResponse;
import com.shulehub.backend.teacher_assignment.model.dto.ClassTeacherSelectionDTO;
import com.shulehub.backend.teacher_assignment.model.dto.SubjectTeacherSelectionDTO;
import com.shulehub.backend.teacher_assignment.service.TeacherAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/teacher-assignments")
@RequiredArgsConstructor
public class TeacherAssignmentController {

    private final TeacherAssignmentService assignmentService;

    /**
     * Recupera la lista di tutti i docenti attivi per la scelta del Class Teacher.
     */
    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'ALL_VIEW', 'CONFIG_VIEW_ROOM')")
    @GetMapping("/eligible-class-teachers")
    public ResponseEntity<ApiResponse<List<ClassTeacherSelectionDTO>>> getEligibleClassTeachers() {
        List<ClassTeacherSelectionDTO> teachers = assignmentService.getEligibleClassTeachers();
        return ResponseEntity.ok(new ApiResponse<>(true, "Lista docenti idonei recuperata", teachers));
    }

    /**
     * Recupera la lista dei docenti abilitati a insegnare una specifica materia.
     */
    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'ALL_VIEW', 'CONFIG_VIEW_ROOM')")
    @GetMapping("/eligible-teachers")
    public ResponseEntity<ApiResponse<List<SubjectTeacherSelectionDTO>>> getEligibleTeachersForSubject(
            @RequestParam Short subjectId) {
        List<SubjectTeacherSelectionDTO> teachers = assignmentService.getEligibleTeachersForSubject(subjectId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lista docenti abilitati recuperata", teachers));
    }

    /**
     * Assegna o cambia il Class Teacher per una YearRoom.
     * Usiamo PATCH perché stiamo modificando solo una parte specifica della configurazione della stanza.
     */
    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'CONFIG_EDIT_ROOM')")
    @PatchMapping("/year-rooms/{yearRoomId}/class-teacher")
    public ResponseEntity<ApiResponse<Void>> assignClassTeacher(
            @PathVariable Integer yearRoomId,
            @RequestBody TeacherSelectionRequest request) {
        
        assignmentService.assignClassTeacher(yearRoomId, request.employeeId());
        return ResponseEntity.ok(new ApiResponse<>(true, "Class Teacher assegnato con successo", null));
    }

    /**
     * Assegna un docente a una materia specifica (Staffing) in una YearRoom.
     */
    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'CONFIG_EDIT_ROOM')")
    @PutMapping("/year-rooms/{yearRoomId}/subjects/{subjectId}")
    public ResponseEntity<ApiResponse<Void>> assignSubjectTeacher(
            @PathVariable Integer yearRoomId,
            @PathVariable Short subjectId,
            @RequestBody TeacherSelectionRequest request) {
        
        assignmentService.assignSubjectTeacher(yearRoomId, subjectId, request.employeeId());
        return ResponseEntity.ok(new ApiResponse<>(true, "Docente assegnato alla materia con successo", null));
    }

    /**
     * DTO interno per ricevere l'ID dell'impiegato dal frontend.
     */
    public record TeacherSelectionRequest(UUID employeeId) {}
}