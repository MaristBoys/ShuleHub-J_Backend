package com.shulehub.backend.subject.controller;

import com.shulehub.backend.common.response.ApiResponse;
import com.shulehub.backend.subject.model.entity.Subject;
import com.shulehub.backend.subject.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    /**
     * Recupera tutte le materie (Sia attive che non) per la gestione in tabella.
     */
    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'ALL_VIEW', 'CONFIG_VIEW_SUBJECTS')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Subject>>> getAllSubjects() {
        List<Subject> subjects = subjectService.getAllSubjects();
        return ResponseEntity.ok(new ApiResponse<>(true, "Tutte le materie recuperate con successo", subjects));
    }

    /**
     * Recupera solo le materie attive (Utile per dropdown o selettori).
     */
    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'ALL_VIEW')")
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<Subject>>> getActiveSubjects() {
        List<Subject> subjects = subjectService.getActiveSubjects();
        return ResponseEntity.ok(new ApiResponse<>(true, "Materie attive recuperate con successo", subjects));
    }

    /**
     * Crea una nuova materia.
     */
    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'CONFIG_EDIT_SUBJECTS')")
    @PostMapping
    public ResponseEntity<ApiResponse<Subject>> createSubject(@RequestBody Subject subject) {
        Subject newSubject = subjectService.createSubject(subject);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Materia creata con successo", newSubject));
    }

    /**
     * Aggiorna i dettagli di una materia esistente.
     */
    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'CONFIG_EDIT_SUBJECTS')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Subject>> updateSubject(
            @PathVariable Short id, 
            @RequestBody Subject subject) {
        Subject updated = subjectService.updateSubject(id, subject);
        return ResponseEntity.ok(new ApiResponse<>(true, "Materia aggiornata con successo", updated));
    }

    /**
     * Toggle rapido dello stato attivo/disattivo.
     */
    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'CONFIG_EDIT_SUBJECTS')")
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<Void>> toggleSubject(@PathVariable Short id) {
        subjectService.toggleSubjectStatus(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Stato materia modificato con successo", null));
    }
}