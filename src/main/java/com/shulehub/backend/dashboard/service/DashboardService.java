// Questo servizio è responsabile di aggregare i dati da diversi domini (SchoolConfig, Registry)
//  per fornire una vista di primo livello alla dashboard.
// L'idea è che la dashboard abbia bisogno di mostrare un riepilogo della configurazione scolastica,
//  del personale e degli studenti, e questi dati sono sparsi in diversi moduli.
// Il DashboardService funge da "orchestratore" che
//  chiama i servizi dei vari moduli, recupera i dati necessari e li combina in un unico DTO (DashboardSummaryDTO)
//  che viene poi restituito al controller per essere inviato al frontend.


package com.shulehub.backend.dashboard.service;

import com.shulehub.backend.dashboard.model.dto.DashboardSummaryDTO;
import com.shulehub.backend.school_config.model.dto.SchoolConfigSummaryDTO;
import com.shulehub.backend.school_config.service.SchoolConfigService;
import com.shulehub.backend.registry.service.EmployeeService;
import com.shulehub.backend.registry.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SchoolConfigService schoolConfigService;
    private final EmployeeService employeeService;
    private final StudentService studentService;

    /**
     * Aggrega i dati da diversi domini per fornire una vista di primo livello.
     * Utilizza l'anno attivo recuperato da schoolConfig per filtrare le altre feature.
     */
    @Transactional(readOnly = true)
    public DashboardSummaryDTO getCombinedDashboardSummary() {
        // 1. Recuperiamo la configurazione scolastica (Anno, Stanze, Materie)
        SchoolConfigSummaryDTO schoolSummary = schoolConfigService.getSchoolConfigSummary();
        
        // 2. Estraiamo l'anno corrente per passarlo come filtro temporale
        Short currentYear = schoolSummary.getCurrentYear();

        // 3. Chiamiamo i servizi degli altri moduli (Registry)
        var employeeSummary = employeeService.getEmployeeSummary(currentYear);
        var studentSummary = studentService.getStudentSummary(currentYear);

        // 4. Assembliamo il DTO finale
        return new DashboardSummaryDTO(
            schoolSummary,
            employeeSummary,
            studentSummary
        );
    }
}