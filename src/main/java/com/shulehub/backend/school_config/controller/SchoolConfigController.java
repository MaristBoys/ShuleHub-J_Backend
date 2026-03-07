package com.shulehub.backend.school_config.controller;

import com.shulehub.backend.common.response.ApiResponse;
import com.shulehub.backend.school_config.model.entity.Year;
import com.shulehub.backend.school_config.service.SchoolConfigService;
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
}
