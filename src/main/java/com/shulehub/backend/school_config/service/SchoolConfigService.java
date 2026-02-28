package com.shulehub.backend.school_config.service;

import com.shulehub.backend.school_config.model.dto.SchoolConfigSummaryDTO;
import com.shulehub.backend.school_config.model.entity.Year;
import com.shulehub.backend.school_config.repository.YearRepository;
import com.shulehub.backend.school_config.repository.YearRoomRepository;
import com.shulehub.backend.subject.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SchoolConfigService {

    private final YearRepository yearRepository;
    private final YearRoomRepository yearRoomRepository;
    private final SubjectRepository subjectRepository;

    /**
     * Recupera il riepilogo per la card School Config.
     * Gestisce la logica di trovare l'anno attivo e contare le risorse collegate.
     */
    @Transactional(readOnly = true)
    public SchoolConfigSummaryDTO getSchoolConfigSummary() {
        
        // 1. Recuperiamo l'anno attivo (fondamentale per contestualizzare i conteggi)
        Year activeYear = yearRepository.findByYearIsActiveTrue()
                .orElseThrow(() -> new RuntimeException("Configurazione Errata: Nessun anno scolastico attivo nel database."));

        // 2. Contiamo le stanze configurate per questo specifico anno
        // Usiamo l'ID dell'anno attivo per filtrare la tabella cfg_year_room
        long roomsCount = yearRoomRepository.countByYearId(activeYear.getId());

        // 3. Contiamo le materie attive nel sistema
        long subjectsCount = subjectRepository.countBySubjectIsActiveTrue();

        // 4. Restituiamo il sotto-DTO
        return new SchoolConfigSummaryDTO(
                activeYear.getYear(),
                roomsCount,
                subjectsCount
        );
    }
}