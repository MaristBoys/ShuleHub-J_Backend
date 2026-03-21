// è il DTO di primo livello della dashboard (i dati che si vedeno nella card della dashboard principale), 
// contiene solo i dati essenziali per mostrare il riepilogo della configurazione scolastica

package com.shulehub.backend.school_config.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchoolConfigSummaryDTO {
    private Short yearId;
    private Short currentYear;
    private long activeRoomsCount; // Quelle con isActive = true
    private long totalRoomsCount;  // Tutte quelle a sistema per l'anno
    private long totalSubjectsCount;
}