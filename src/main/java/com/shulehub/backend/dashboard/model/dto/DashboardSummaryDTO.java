
// Questo oggetto è il contratto tra Backend e Frontend relativo alla dashboard.
// Contiene i dati aggregati  di primo livello che servono per mostrare il riepilogo della configurazione scolastica, del personale e degli studenti
// Contiene i tre sotto-DTO che abbiamo progettato.

package com.shulehub.backend.dashboard.model.dto;

import com.shulehub.backend.school_config.model.dto.SchoolConfigSummaryDTO;
import com.shulehub.backend.registry.model.dto.EmployeeSummaryDTO;
import com.shulehub.backend.registry.model.dto.StudentSummaryDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDTO {
    private SchoolConfigSummaryDTO school;
    private EmployeeSummaryDTO employees;
    private StudentSummaryDTO students;
}