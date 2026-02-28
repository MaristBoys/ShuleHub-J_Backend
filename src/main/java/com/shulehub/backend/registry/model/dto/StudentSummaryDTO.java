package com.shulehub.backend.registry.model.dto;
//utilizzato per la dashboard, contiene i dati aggregati sugli studenti

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentSummaryDTO {
    private long activeStudentsCount;
    private long enrolledThisYearCount;
    private long leftThisYearCount;
}