// è il DTO di primo livello della dashboard (i dati che si vedeno nella card), 
// contiene solo i dati essenziali per mostrare il riepilogo della configurazione scolastica

package com.shulehub.backend.school_config.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchoolConfigSummaryDTO {
    private Short currentYear;
    private long activeRoomsCount;
    private long totalSubjectsCount;
}