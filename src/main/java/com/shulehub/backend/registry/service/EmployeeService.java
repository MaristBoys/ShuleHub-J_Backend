package com.shulehub.backend.registry.service;

import com.shulehub.backend.registry.model.dto.EmployeeSummaryDTO;
import com.shulehub.backend.registry.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    /**
     * Calcola le statistiche degli impiegati per la dashboard.
     * * @param year L'anno scolastico di riferimento (Short)
     * @return DTO con il totale attivi, assunti nell'anno e cessati nell'anno
     */
    @Transactional(readOnly = true)
    public EmployeeSummaryDTO getEmployeeSummary(Short year) {
        
        // 1. Definiamo l'intervallo temporale basato sull'anno fornito
        // Esempio: se year è 2025, otteniamo 2025-01-01 e 2025-12-31
        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        LocalDate endOfYear = LocalDate.of(year, 12, 31);

        // 2. Eseguiamo i conteggi tramite il repository
        // Il numero totale di impiegati attualmente attivi (stato booleano)
        long activeCount = employeeRepository.countByEmployeeIsActiveTrue();

        // Il numero di impiegati assunti (hire_date) durante l'anno selezionato
        long enrolledCount = employeeRepository.countByHireDateBetween(startOfYear, endOfYear);

        // Il numero di impiegati che hanno terminato il rapporto (employment_end_date) nell'anno
        long terminatedCount = employeeRepository.countByEmploymentEndDateBetween(startOfYear, endOfYear);

        // 3. Restituiamo il DTO popolato
        return new EmployeeSummaryDTO(
                activeCount,
                enrolledCount,
                terminatedCount
        );
    }
}
