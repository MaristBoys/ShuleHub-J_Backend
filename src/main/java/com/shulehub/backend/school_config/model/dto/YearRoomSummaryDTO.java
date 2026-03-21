package com.shulehub.backend.school_config.model.dto;

//contiene i dati che vengono inseriti nella singola card all'interno della matrice delle stanze (RoomMatrix)
// contiene solo i dati essenziali per mostrare il riepilogo della stanza nella matrice (RoomMatrix)
// e anche i campi aggiuntivi per la Dashboard (conteggio studenti, nome insegnante, rapporto di organico)

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YearRoomSummaryDTO {
    private Integer yearRoomId;
    private String roomName;
    private boolean isAssigned;
    private Boolean isActive; // yearroom_is_active
    
    // Nuovi campi per la Dashboard
    private Integer studentCount;
    private UUID classTeacherId;
    private String classTeacherName;
    private String staffingRatio; // Esempio: "8/10"
    private Double staffingPercentage; // Utile per colorare i badge (es. 0.8)
}