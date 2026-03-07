package com.shulehub.backend.school_config.model.dto;

// è una visualizzazione semplificata della YearRoom,
// contiene solo i dati essenziali per mostrare il riepilogo della stanza nella matrice (RoomMatrix)

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class YearRoomSummaryDTO {
    private Integer id; // ID della YearRoom (null se la cella è vuota)
    private String roomName;
    private boolean isAssigned;
}