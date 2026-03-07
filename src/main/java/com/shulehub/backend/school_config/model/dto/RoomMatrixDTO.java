// src/main/java/com/shulehub/backend/school_config/model/dto/RoomMatrixDTO.jav
package com.shulehub.backend.school_config.model.dto;

// si tratta del DTO che rappresenta la matrice delle stanze (RoomMatrix)
// Contiene una lista di Stream (colonne, le sezioni in Italia A, B ma sono numeri)
//  e una lista di FormRowDTO (righe che sono i Form da 1 a 6),
// dove ogni FormRowDTO contiene il numero della Form, il nome della Form e 
// una mappa che associa ogni Stream al dettaglio della stanza (YearRoomSummaryDTO)

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class RoomMatrixDTO {
    private List<Short> streams; // Intestazioni colonne (1, 2, 3...)
    private List<FormRowDTO> rows; // Righe della matrice
}



