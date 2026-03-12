package com.shulehub.backend.indicator_scale.controller;

import com.shulehub.backend.common.response.ApiResponse;
import com.shulehub.backend.indicator_scale.model.dto.IndicatorScaleDTO;
import com.shulehub.backend.indicator_scale.service.IndicatorScaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/indicator-scales")
@RequiredArgsConstructor
public class IndicatorScaleController {

    private final IndicatorScaleService indicatorScaleService;

    /**
     * Recupera le scale disponibili filtrate per tipo (es. GRADE, DIVISION, CONDUCT).
     * Usato per popolare i dropdown di selezione nelle varie parti dell'app.
     */
    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'ALL_VIEW', 'CONFIG_VIEW_ROOMS')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<IndicatorScaleDTO>>> getAvailableScales(@RequestParam String type) {
        List<IndicatorScaleDTO> scales = indicatorScaleService.getAvailableScales(type);
        return ResponseEntity.ok(new ApiResponse<>(true, "Scale recuperate con successo", scales));
    }


    /**
     * Recupera TUTTE le scale attive nel sistema (senza filtro per tipo).
     */
    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'ALL_VIEW', 'CONFIG_VIEW_ROOMS')")
    @GetMapping("/all-active")
    public ResponseEntity<ApiResponse<List<IndicatorScaleDTO>>> getAllActiveScales() {
        List<IndicatorScaleDTO> scales = indicatorScaleService.getAllActiveScales();
        return ResponseEntity.ok(new ApiResponse<>(true, "Tutte le scale attive recuperate", scales));
    }
}
