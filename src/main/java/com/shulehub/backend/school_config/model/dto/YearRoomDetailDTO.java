package com.shulehub.backend.school_config.model.dto;
// Contiene i dettagli completi di una stanza (YearRoom) per la visualizzazione nella pagina di dettaglio della stanza.
// Include i dati identificativi, le scale attualmente assegnate e i suggerimenti per le scale da assegnare.

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class YearRoomDetailDTO {
    // Dati identificativi
    private Integer yearRoomId;
    private String roomName;
    private String formName;

    // Scale Attuali (ID e Nomi per display immediato)
    private SelectedScales currentScales;

    // Suggerimenti (ID della scala suggerita per tipo)
    // Es: { "GRADE": 1, "DIVISION": 3, "CONDUCT": 5 }
    private Map<String, Short> suggestedScaleIds;

    @Data
    @Builder
    public static class SelectedScales {
        private Short gradeScaleId;
        private String gradeScaleName;
        
        private Short divisionScaleId;
        private String divisionScaleName;
        
        private Short conductAlphaScaleId;
        private String conductAlphaScaleName;
        
        private Short conductTextScaleId;
        private String conductTextScaleName;
    }
}