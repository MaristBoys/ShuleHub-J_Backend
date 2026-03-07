package com.shulehub.backend.school_config.model.dto;

//serva a rappresentare una riga della matrice delle stanze (RoomMatrix), contiene il numero della Form,
//  il nome della Form e una mappa che associa ogni Stream al dettaglio della stanza (YearRoomSummaryDTO)

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Map;

@Data
@AllArgsConstructor
public class FormRowDTO {
    private Short formNum;
    private String formName;
    private Map<Short, YearRoomSummaryDTO> cells; // StreamNum -> Dettaglio Stanza
}
