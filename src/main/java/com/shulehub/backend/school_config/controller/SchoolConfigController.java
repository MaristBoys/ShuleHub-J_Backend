package com.shulehub.backend.school_config.controller;

import com.shulehub.backend.common.response.ApiResponse;
import com.shulehub.backend.school_config.model.entity.Year;
import com.shulehub.backend.school_config.service.SchoolConfigService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @GetMapping("/years")
    public ResponseEntity<ApiResponse<List<Year>>> getAllYears() {
        List<Year> years = schoolConfigService.getAllYears();
        return ResponseEntity.ok(new ApiResponse<>(true, "Years retrieved", years));
    }

    /**
     * NUOVO: Crea il prossimo anno accademico (POST)
     * Questo risponde alla chiamata del tuo service JS
     */
    @PostMapping("/years")
    public ResponseEntity<ApiResponse<Year>> createNextYear() {
        Year newYear = schoolConfigService.createNextYear();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Year created: " + newYear.getYear(), newYear));
    }


    /**
     * Endpoint per attivare un anno specifico
     */
    @PatchMapping("/years/{id}/activate")
    public ResponseEntity<ApiResponse<Void>> activateYear(@PathVariable Short id) {
        schoolConfigService.updateActiveYear(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Active year updated successfully", null));
    }
}
