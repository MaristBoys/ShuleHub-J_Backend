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
/*
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
 */
     // --- ROOM MODAL DETAILS (toggle che gestisce lo stato della yearRoom) ---
    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'ALL_VIEW', 'CONFIG_EDIT_ROOM')")
    @PatchMapping("/rooms/{id}/status")
    public ResponseEntity<ApiResponse<Void>> toggleRoomStatus(
            @PathVariable Integer id, 
            @RequestParam Boolean active) {
        schoolConfigService.updateRoomStatus(id, active);
        return ResponseEntity.ok(new ApiResponse<>(true, "Stato stanza aggiornato", null));
    }
    
/* -- sostituito dal successivo
    // --- ROOM MODAL DETAILS (Dati aggregati per il modale per una room esistente) ---
    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'ALL_VIEW', 'CONFIG_VIEW_ROOM')")
    @GetMapping("/rooms/{id}/details")
    public ResponseEntity<ApiResponse<YearRoomDetailDTO>> getYearRoomDetails(@PathVariable Integer id) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Details retrieved", schoolConfigService.getYearRoomDetails(id)));
    }
 */
    //
    /** --- ROOM MODAL DETAILS
     * Recupera i dettagli per il modale di configurazione.
     * Funziona in due modalità:
     * 1. Se l'ID nel path esiste: recupera i dati di una stanza già configurata.
     * 2. Se vengono passati roomId e yearId come parametri: genera una preview per una Ghost Cell.
     */
    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'ALL_VIEW', 'CONFIG_VIEW_ROOM')")
    @GetMapping({"/rooms/{id}/details", "/rooms/details/preview"})
    public ResponseEntity<ApiResponse<YearRoomDetailDTO>> getYearRoomDetails(
            @PathVariable(required = false) Integer id,
            @RequestParam(required = false) Short roomId,
            @RequestParam(required = false) Short yearId
    ) {
        // Se non abbiamo né l'ID della configurazione né i parametri della ghost cell, lanciamo errore
        if (id == null && (roomId == null || yearId == null)) {
            throw new RuntimeException("Missing parameters: provide either yearRoomId or both roomId and yearId");
        }

        // Chiamata al service unico che gestisce entrambi i casi
        YearRoomDetailDTO details = schoolConfigService.getYearRoomDetails(id, roomId, yearId);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Details retrieved", details));
    }


    // --- UPDATE ROOM SCALES (Aggiornamento delle scale di valutazione per una stanza) ---
    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'CONFIG_EDIT_ROOM')")
    @PutMapping("/rooms/{id}/scales")
    public ResponseEntity<ApiResponse<Void>> updateRoomScales(@PathVariable Integer id, @RequestBody Map<String, Short> scaleIds) {
        schoolConfigService.updateYearRoomScales(id, scaleIds);
        return ResponseEntity.ok(new ApiResponse<>(true, "Scales updated", null));
    }
}