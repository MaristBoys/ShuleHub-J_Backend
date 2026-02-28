// Questo controller espone un endpoint REST per la dashboard, che restituisce un oggetto DashboardSummaryDTO
//   contenente i dati aggregati necessari per mostrare il riepilogo della configurazione scolastica, del personale e degli studenti.
// Il controller chiama il DashboardService, che si occupa di orchestrare le chiamate ai servizi dei vari moduli (SchoolConfig, Registry) 
// per recuperare i dati necessari e combinarli in un unico DTO da restituire al frontend.

package com.shulehub.backend.dashboard.controller;

import com.shulehub.backend.common.response.ApiResponse;
import com.shulehub.backend.dashboard.model.dto.DashboardSummaryDTO;
import com.shulehub.backend.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DashboardSummaryDTO>> getSummary() {
        // Recuperiamo i dati dal service
        DashboardSummaryDTO summary = dashboardService.getCombinedDashboardSummary();
        
        // Avvolgiamo i dati nella tua struttura standard (claass ApiResponse)
        ApiResponse<DashboardSummaryDTO> response = new ApiResponse<>(
            true, 
            "Dashboard data retrieved successfully", 
            summary
        );
        
        return ResponseEntity.ok(response);
    }
}