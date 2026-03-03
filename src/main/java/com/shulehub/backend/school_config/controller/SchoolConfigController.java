package com.shulehub.backend.school_config.controller;

import com.shulehub.backend.common.response.ApiResponse;
import com.shulehub.backend.school_config.model.entity.Year;
import com.shulehub.backend.school_config.service.SchoolConfigService;
import lombok.RequiredArgsConstructor;
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
     * Endpoint per attivare un anno specifico
     */
    @PatchMapping("/years/{id}/activate")
    public ResponseEntity<ApiResponse<Void>> activateYear(@PathVariable Short id) {
        schoolConfigService.updateActiveYear(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Active year updated successfully", null));
    }
}
