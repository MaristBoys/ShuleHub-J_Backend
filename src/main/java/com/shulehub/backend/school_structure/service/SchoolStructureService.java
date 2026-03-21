package com.shulehub.backend.school_structure.service;

import com.shulehub.backend.school_structure.model.entity.Year;
import com.shulehub.backend.school_structure.model.entity.YearRoom;
import com.shulehub.backend.school_structure.repository.YearRepository;
import com.shulehub.backend.school_structure.model.entity.Form;
import com.shulehub.backend.school_structure.repository.FormRepository;
import com.shulehub.backend.school_structure.repository.YearRoomRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SchoolStructureService {

    private final YearRepository yearRepository;
    private final FormRepository formRepository;
    private final YearRoomRepository yearRoomRepository; 


    /***************************************************************************************************
     YEARS MANAGEMENT
     ****************************************************************************************************/

    // Metodo per recuperare un anno specifico (usato ad esempio per mostrare i dettagli di un anno selezionato)
    @Transactional(readOnly = true)
    public Year getYearById(Short id) {
        return yearRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Year not found with id: " + id));
    }
  
    // Metodo per recuperare l'anno attivo (Utility per l'orchestratore e per i controller)
    @Transactional(readOnly = true)
    public Year getActiveYear() {
        return yearRepository.findByYearIsActiveTrue()
                .orElseThrow(() -> new RuntimeException("Year not found"));
    }


    // Metodo per recuperare la lista completa degli anni (ordinati per anno decrescente)
    @Transactional(readOnly = true)
    public List<Year> getAllYears() {
        return yearRepository.findAllByOrderByYearDesc();
    }

    // Metodo per creare un nuovo anno accademico (incrementando l'ultimo presente)
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
        nextYear.setYearDescription(Integer.toString(nextYearValue)); //String.valueOf(nextYearValue)
        nextYear.setYearIsActive(false);

        return yearRepository.save(nextYear);
    }

    // Metodo per attivare un anno specifico (disattivando quello attivo precedente)
    @Transactional
    public void updateActiveYear(Short yearId) {

        Year year = yearRepository.findById(yearId)
                .orElseThrow(() -> new RuntimeException("Year not found"));

        yearRepository.deactivateActiveYear();

        year.setYearIsActive(true);
        yearRepository.save(year);
    }


    
    /***************************************************************************************************
     FORM MANAGEMENT
     ****************************************************************************************************/

    // Metodo per recuperare la lista completa dei Form (ordinati per numero)
    @Transactional(readOnly = true)
    public List<Form> getAllForms() {
        return formRepository.findAllByOrderByFormNumAsc();
    }
    
    // Metodo per recuperare solo i Form attivi (ordinati per numero)
    @Transactional(readOnly = true)
    public List<Form> getActiveFormsSorted() {
        // Spostiamo qui la logica di accesso al DB per i Form
        return formRepository.findByFormIsActiveTrueOrderByFormNumAsc();
    }


    
    /***************************************************************************************************
     YEAR-ROOM MANAGEMENT
     ****************************************************************************************************/

    @Transactional(readOnly = true)
    public List<YearRoom> getYearRoomsByYearId(Short yearId) {
        return yearRoomRepository.findByYearId(yearId);
    }

    @Transactional(readOnly = true)
    public YearRoom getYearRoomById(Integer id) {
        return yearRoomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("YearRoom non trovata con ID: " + id));
    }

    @Transactional(readOnly = true)
    public long countYearRoomsByYearId(Short yearId) {
        return yearRoomRepository.countByYearId(yearId);
    }

    @Transactional(readOnly = true)
    public long countActiveYearRoomsByYearId(Short yearId) {
        return yearRoomRepository.countByYearIdAndYearRoomIsActiveTrue(yearId);
    }

    @Transactional(readOnly = true)
    public long countTotalYearRoomsByYearId(Short yearId) {
        return yearRoomRepository.countByYearId(yearId);
    }

     @Transactional
    public void saveYearRoom(YearRoom yearRoom) {
        yearRoomRepository.save(yearRoom);
    }


}