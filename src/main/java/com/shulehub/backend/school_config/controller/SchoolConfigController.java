package com.shulehub.backend.school_config.controller;

import com.shulehub.backend.common.response.ApiResponse;
import com.shulehub.backend.school_config.model.entity.Year;
import com.shulehub.backend.school_config.service.SchoolConfigService;
import com.shulehub.backend.subject.model.entity.Subject;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/school-config")
@RequiredArgsConstructor
public class SchoolConfigController {

    private final SchoolConfigService schoolConfigService;


    // --- GESTIONE CURRENT YEAR ---

    /**
     * Endpoint per la lista completa degli anni
     */
    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'ALL_VIEW', 'DASHBOARD_VIEW_CONFIG')")
    @GetMapping("/years")
    public ResponseEntity<ApiResponse<List<Year>>> getAllYears() {
        List<Year> years = schoolConfigService.getAllYears();
        return ResponseEntity.ok(new ApiResponse<>(true, "Years retrieved", years));
    }

    /**
     * Crea il prossimo anno accademico (POST)
     * Questo risponde alla chiamata del service JS
     */
    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'CONFIG_EDIT_YEAR')") 
    // Solo chi ha il permesso di editare gli anni o ALL_ACCESS può creare un nuovo anno
    // recupero il permesso dalle authorities del JWT che abbiamo caricato nel filtro di autenticazione
    @PostMapping("/years")
    public ResponseEntity<ApiResponse<Year>> createNextYear() {
        Year newYear = schoolConfigService.createNextYear();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Year created: " + newYear.getYear(), newYear));
    }


    /**
     * Endpoint per attivare un anno specifico
     */
    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'CONFIG_EDIT_YEAR')") 
    // Solo chi ha il permesso di editare gli anni o ALL_ACCESS può attivare un anno
    // recupero il permesso dalle authorities del JWT che abbiamo caricato nel filtro di autenticazione
    @PatchMapping("/years/{id}/activate")
    public ResponseEntity<ApiResponse<Void>> activateYear(@PathVariable Short id) {
        schoolConfigService.updateActiveYear(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Active year updated successfully", null));
    }

    // --- GESTIONE SUBJECTS ---

    /**
     * Recupera la lista completa di tutte le materie (Sola Lettura)
     */
    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'ALL_VIEW', 'DASHBOARD_VIEW_CONFIG')")
    @GetMapping("/subjects")
    public ResponseEntity<ApiResponse<List<Subject>>> getAllSubjects() {
        List<Subject> subjects = schoolConfigService.getAllSubjects();
        return ResponseEntity.ok(new ApiResponse<>(true, "Subjects list retrieved", subjects));
    }

    /**
     * Crea una nuova materia
     */
    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'CONFIG_EDIT_SUBJECTS')")
    @PostMapping("/subjects")
    public ResponseEntity<ApiResponse<Subject>> createSubject(@RequestBody Subject subject) {
        Subject newSubject = schoolConfigService.createSubject(subject);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Subject created successfully", newSubject));
    }

    /**
     * Aggiorna una materia esistente (Dettagli completi)
     */
    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'CONFIG_EDIT_SUBJECTS')")
    @PutMapping("/subjects/{id}")
    public ResponseEntity<ApiResponse<Subject>> updateSubject(@PathVariable Short id, @RequestBody Subject subject) {
        Subject updated = schoolConfigService.updateSubject(id, subject);
        return ResponseEntity.ok(new ApiResponse<>(true, "Subject updated successfully", updated));
    }

    /**
     * Toggle rapido dello stato attivo/disattivo
     */
    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'CONFIG_EDIT_SUBJECTS')")
    @PatchMapping("/subjects/{id}/toggle")
    public ResponseEntity<ApiResponse<Void>> toggleSubject(@PathVariable Short id) {
        schoolConfigService.toggleSubjectStatus(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Subject status toggled", null));
    }






}
