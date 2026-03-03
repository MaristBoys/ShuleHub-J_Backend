package com.shulehub.backend.school_config.service;

import com.shulehub.backend.school_config.model.dto.SchoolConfigSummaryDTO;
import com.shulehub.backend.school_config.model.entity.Year;
import com.shulehub.backend.school_config.repository.YearRepository;
import com.shulehub.backend.school_config.repository.YearRoomRepository;
import com.shulehub.backend.subject.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

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

    // Aggiunte a SchoolConfigService.java

    /**
     * Recupera tutti gli anni registrati nel sistema
     */
    @Transactional(readOnly = true)
    public List<Year> getAllYears() {
        return yearRepository.findAllByOrderByYearDesc(); 
    }

    /**
     * Cambia l'anno attivo del sistema.
     * @param yearId ID dell'anno da attivare
     */
    @Transactional
    public void updateActiveYear(Short yearId) {
        // 1. Troviamo l'anno che deve diventare attivo
        Year newActiveYear = yearRepository.findById(yearId)
                .orElseThrow(() -> new RuntimeException("Year not found"));

        // 2. Se è già attivo, non facciamo nulla
        if (newActiveYear.isYearIsActive()) return;

        // 3. Troviamo l'anno attualmente attivo e lo disattiviamo
        yearRepository.findByYearIsActiveTrue().ifPresent(oldActive -> {
            oldActive.setYearIsActive(false);
            yearRepository.save(oldActive);
        });

        // 4. Attiviamo il nuovo anno
        newActiveYear.setYearIsActive(true);
        yearRepository.save(newActiveYear);
        
        // Nota: @Transactional assicura che se qualcosa fallisce, 
        // non rimaniamo con due anni attivi o nessuno.
    }

    @Transactional
    public Year createNextYear() {
        // 1. Recupera l'ultimo anno presente
        int lastYearValue = yearRepository.findFirstByOrderByYearDesc()
                .map(Year::getYear)
                .orElse((short)2026); // Default se il DB è vuoto

        int nextYearValue = lastYearValue + 1;

        // 2. Crea il nuovo record (non attivo di default)
        Year nextYear = new Year();
        nextYear.setYear((short) nextYearValue);
        nextYear.setYearDescription("Academic Session " + nextYearValue);
        nextYear.setYearIsActive(false);

        return yearRepository.save(nextYear);
    }


}