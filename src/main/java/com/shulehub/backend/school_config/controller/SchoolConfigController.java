package com.shulehub.backend.school_config.controller;

import com.shulehub.backend.common.response.ApiResponse;
import com.shulehub.backend.school_config.model.dto.RoomMatrixDTO;
import com.shulehub.backend.school_config.model.dto.YearRoomDetailDTO;
import com.shulehub.backend.school_config.service.SchoolConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/school-config")
@RequiredArgsConstructor
public class SchoolConfigController {
    
    private final SchoolConfigService schoolConfigService;

    // --- ROOMS MATRIX (La griglia principale) ---
    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'ALL_VIEW', 'CONFIG_VIEW_ROOMS')")
    @GetMapping("/rooms/matrix/{yearId}")
    public ResponseEntity<ApiResponse<RoomMatrixDTO>> getRoomMatrix(@PathVariable Short yearId) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Matrix retrieved", schoolConfigService.getRoomMatrix(yearId)));
    }

    // --- ROOM MODAL DETAILS (dto per il preview prima di creare la room chiamato dalla ghost cell) ---
    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'ALL_VIEW', 'CONFIG_VIEW_ROOM')")
    @GetMapping("/rooms/preview")
    public ResponseEntity<ApiResponse<YearRoomDetailDTO>> getRoomPreview(
            @RequestParam Short yearId, 
            @RequestParam Short roomNum) {
        
        return ResponseEntity.ok(new ApiResponse<>(
            true, 
            "Preview data generated", 
            schoolConfigService.getNewYearRoomPreview(yearId, roomNum)
        ));
    }

     // --- ROOM MODAL DETAILS (toggle che gestisce lo stato della yearRoom) ---
    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'ALL_VIEW', 'CONFIG_EDIT_ROOM')")
    @PatchMapping("/rooms/{id}/status")
    public ResponseEntity<ApiResponse<Void>> toggleRoomStatus(
            @PathVariable Integer id, 
            @RequestParam Boolean active) {
        schoolConfigService.updateRoomStatus(id, active);
        return ResponseEntity.ok(new ApiResponse<>(true, "Stato stanza aggiornato", null));
    }
    
    // --- ROOM MODAL DETAILS (Dati aggregati per il modale per una room esistente) ---
    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'ALL_VIEW', 'CONFIG_VIEW_ROOM')")
    @GetMapping("/rooms/{id}/details")
    public ResponseEntity<ApiResponse<YearRoomDetailDTO>> getYearRoomDetails(@PathVariable Integer id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Details retrieved", schoolConfigService.getYearRoomDetails(id)));
    }

    // --- UPDATE ROOM SCALES (Aggiornamento delle scale di valutazione per una stanza) ---
    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'CONFIG_EDIT_ROOM')")
    @PutMapping("/rooms/{id}/scales")
    public ResponseEntity<ApiResponse<Void>> updateRoomScales(@PathVariable Integer id, @RequestBody Map<String, Short> scaleIds) {
        schoolConfigService.updateYearRoomScales(id, scaleIds);
        return ResponseEntity.ok(new ApiResponse<>(true, "Scales updated", null));
    }
}