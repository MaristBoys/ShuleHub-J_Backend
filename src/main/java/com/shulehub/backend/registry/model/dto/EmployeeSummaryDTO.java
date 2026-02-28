
//utilizzato per la dashboard, contiene i dati aggregati sugli impiegati
package com.shulehub.backend.registry.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSummaryDTO {
    private long activeEmployeesCount;
    private long enrolledThisYearCount;
    private long terminatedThisYearCount;
}