package com.shulehub.backend.school_config.service;

import com.shulehub.backend.indicator_scale.model.dto.IndicatorScaleDTO;
import com.shulehub.backend.indicator_scale.repository.IndicatorScaleRangeRepository;
import com.shulehub.backend.indicator_scale.repository.IndicatorScaleRepository;
import com.shulehub.backend.school_config.model.dto.FormRowDTO;
import com.shulehub.backend.school_config.model.dto.RoomMatrixDTO;
import com.shulehub.backend.school_config.model.dto.SchoolConfigSummaryDTO;
import com.shulehub.backend.school_config.model.dto.YearRoomDetailDTO;
import com.shulehub.backend.school_config.model.dto.YearRoomSummaryDTO;
import com.shulehub.backend.school_config.repository.YearRoomDetailViewRepository;
import com.shulehub.backend.school_config.model.view.YearRoomDetailView;
import com.shulehub.backend.school_config.model.view.YearRoomStatsView;
import com.shulehub.backend.school_config.repository.YearRoomStatsViewRepository;
import com.shulehub.backend.school_structure.model.entity.Form;
import com.shulehub.backend.school_structure.model.entity.Year;
import com.shulehub.backend.school_structure.model.entity.YearRoom;
import com.shulehub.backend.school_structure.repository.FormRepository;
import com.shulehub.backend.school_structure.repository.YearRepository;
import com.shulehub.backend.school_structure.repository.YearRoomRepository;
import com.shulehub.backend.subject.model.entity.Subject;
import com.shulehub.backend.subject.repository.SubjectRepository;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SchoolConfigService_copy {

    private final YearRepository yearRepository;
    private final YearRoomRepository yearRoomRepository;
    private final YearRoomStatsViewRepository yearRoomStatsViewRepository;

    private final YearRoomDetailViewRepository yearRoomDetailViewRepository;
    private final IndicatorScaleRepository indicatorScaleRepository;
    private final IndicatorScaleRangeRepository indicatorScaleRangeRepository;

    private final SubjectRepository subjectRepository;
    private final FormRepository formRepository;

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
                activeYear.getId(),
                activeYear.getYear(),
                roomsCount,
                subjectsCount
        );
    }

    /*************************************************************************************************** 
    YEARS
    ****************************************************************************************************/

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
        nextYear.setYearDescription(Integer.toString(nextYearValue)); //String.valueOf(nextYearValue)
        nextYear.setYearIsActive(false);

        return yearRepository.save(nextYear);
    }

    /*************************************************************************************************** 
    ROOMS
    ****************************************************************************************************/
    @Transactional(readOnly = true)
    public RoomMatrixDTO getRoomMatrix(Short yearId) {
        // 1. Carichiamo le stanze e le statistiche aggregate in "Bulk"
        List<YearRoom> yearRooms = yearRoomRepository.findByYearId(yearId);
        List<YearRoomStatsView> allStats = yearRoomStatsViewRepository.findByYearId(yearId);

        // 2. Trasformiamo le statistiche in una mappa per accesso rapido via ID YearRoom
        Map<Integer, YearRoomStatsView> statsMap = allStats.stream()
            .collect(Collectors.toMap(YearRoomStatsView::getYearRoomId, s -> s));

        // 3. Mappa per posizionare le stanze nella griglia (Key: formId-streamNum)
        Map<String, YearRoom> roomMap = new HashMap<>();
        for (YearRoom yr : yearRooms) {
            if (yr.getRoom() != null && yr.getRoom().getForm() != null) {
                // Estraiamo lo stream dall'ultima cifra del numero stanza (es. 21 -> 1)
                int streamNum = yr.getRoom().getRoomNum() % 10; 
                String key = yr.getRoom().getForm().getId() + "-" + streamNum;
                roomMap.put(key, yr);
            }
        }

        // 4. Definiamo le colonne (Stream 1, 2, 3) e le righe (Form attivi)
        List<Short> streams = List.of((short)1, (short)2, (short)3);
        List<Form> allForms = formRepository.findByFormIsActiveTrueOrderByFormNumAsc();

        // 5. Costruzione dinamica delle righe della matrice
        List<FormRowDTO> rows = new ArrayList<>();
        for (Form form : allForms) {
            Map<Short, YearRoomSummaryDTO> cells = new HashMap<>();
            
            for (Short sNum : streams) {
                YearRoom match = roomMap.get(form.getId() + "-" + sNum);
                
                if (match != null) {
                    YearRoomStatsView stats = statsMap.get(match.getId());
                    
                    // Prepariamo i dati calcolati per il frontend
                    String ratio = "0/0";
                    Double percentage = 0.0;
                    if (stats != null && stats.getTotalSubjects() != null && stats.getTotalSubjects() > 0) {
                        ratio = stats.getAssignedSubjects() + "/" + stats.getTotalSubjects();
                        percentage = (double) stats.getAssignedSubjects() / stats.getTotalSubjects();
                    }

                    cells.put(sNum, new YearRoomSummaryDTO(
                        match.getId(), 
                        match.getRoom().getRoomName(), 
                        true, // isAssigned
                        stats != null ? stats.getStudentCount() : 0,
                        stats != null ? stats.getClassTeacherId() : null,
                        stats != null ? stats.getClassTeacherName() : "No CT assigned",
                        ratio,
                        percentage
                    ));
                } else {
                    // Cella vuota: il frontend mostrerà il tasto "+"
                    cells.put(sNum, new YearRoomSummaryDTO(null, null, false, 0, null, null, null, 0.0));
                }
            }
            rows.add(new FormRowDTO(form.getFormNum(), form.getFormName(), cells));
        }

        return new RoomMatrixDTO(streams, rows);
    }



        /**
     * Recupera i dettagli completi della stanza per il Tab 1, 
     * inclusi i suggerimenti automatici per le scale.
     */
    @Transactional(readOnly = true)
    public YearRoomDetailDTO getYearRoomDetails(Integer yearRoomId) {
        // 1. Recupero i dati piatti dalla Vista
        YearRoomDetailView view = yearRoomDetailViewRepository.findByYearRoomId(yearRoomId)
                .orElseThrow(() -> new RuntimeException("YearRoom not found with ID: " + yearRoomId));

        // 2. Costruisco l'oggetto delle scale attuali
        YearRoomDetailDTO.SelectedScales currentScales = YearRoomDetailDTO.SelectedScales.builder()
                .gradeScaleId(view.getGradeScaleId())
                .gradeScaleName(view.getGradeScaleName())
                .divisionScaleId(view.getDivisionScaleId())
                .divisionScaleName(view.getDivisionScaleName())
                .conductAlphaScaleId(view.getConductAlphaScaleId())
                .conductAlphaScaleName(view.getConductAlphaScaleName())
                .conductTextScaleId(view.getConductTextScaleId())
                .conductTextScaleName(view.getConductTextScaleName())
                .build();

        // 3. Calcolo i suggerimenti (Suggested)
        Map<String, Short> suggestions = calculateSuggestedScales(view);

        return YearRoomDetailDTO.builder()
                .yearRoomId(view.getYearRoomId())
                .roomName(view.getRoomName())
                .formNum(view.getFormNum())
                .currentScales(currentScales)
                .suggestedScaleIds(suggestions)
                .build();
    }

    /**
     * Logica di Smart Defaulting per le scale.
     */
    private Map<String, Short> calculateSuggestedScales(YearRoomDetailView currentView) {
        Map<String, Short> suggestions = new HashMap<>();
        
        // Priorità 1: Rollover (Cerca la stessa stanza l'anno precedente)
        short previousYearId = (short) (currentView.getYearId() - 1);
        yearRoomRepository.findByYearIdAndRoomId(previousYearId, currentView.getRoomId())
            .ifPresent(prevRoom -> {
                suggestions.put("GRADE", prevRoom.getGradeScale().getId());
                suggestions.put("DIVISION", prevRoom.getDivisionScale().getId());
                if(prevRoom.getConductAlphaScale() != null) 
                    suggestions.put("CONDUCT_ALPHA", prevRoom.getConductAlphaScale().getId());
            });

        // Priorità 2: Se mancano suggerimenti, usa il range del Form
        if (!suggestions.containsKey("GRADE")) {
            findTopSuggested(suggestions, "GRADE", currentView.getFormNum());
        }
        if (!suggestions.containsKey("DIVISION")) {
            findTopSuggested(suggestions, "DIVISION", currentView.getFormNum());
        }

        return suggestions;
    }

    private void findTopSuggested(Map<String, Short> map, String type, Short formNum) {
        indicatorScaleRepository.findSuggestedScales(type, formNum).stream()
            .findFirst()
            .ifPresent(scale -> map.put(type, scale.getId()));
    }

    /**
     * Recupera la lista di tutte le scale attive per tipo, 
     * includendo i range per i popover informativi.
     */
    @Transactional(readOnly = true)
    public List<IndicatorScaleDTO> getAvailableScales(String type) {
        return indicatorScaleRepository.findByIndicatorTypeAndScaleIsActiveTrue(type).stream()
            .map(scale -> {
                IndicatorScaleDTO dto = new IndicatorScaleDTO();
                dto.setId(scale.getId());
                dto.setScaleName(scale.getScaleName());
                dto.setIndicatorType(scale.getIndicatorType());
                
                // Carica i range per la legenda/popover
                List<IndicatorScaleDTO.ScaleRangeDTO> rangeDtos = indicatorScaleRangeRepository
                    .findByIndicatorScaleIdOrderByMinValueDesc(scale.getId()).stream()
                    .map(r -> {
                        IndicatorScaleDTO.ScaleRangeDTO rdto = new IndicatorScaleDTO.ScaleRangeDTO();
                        rdto.setTextValue(r.getIndicatorTextValue());
                        rdto.setMinValue(r.getMinValue());
                        rdto.setMaxValue(r.getMaxValue());
                        rdto.setAttribute(r.getAttribute());
                        rdto.setPoints(r.getPoints());
                        return rdto;
                    }).collect(Collectors.toList());
                
                dto.setRanges(rangeDtos);
                return dto;
            }).collect(Collectors.toList());
    }

        /**
     * Aggiorna le scale di valutazione per una YearRoom specifica.
     */
    @Transactional
    public void updateYearRoomScales(Integer yearRoomId, Map<String, Short> scaleIds) {
        YearRoom yearRoom = yearRoomRepository.findById(yearRoomId)
                .orElseThrow(() -> new RuntimeException("YearRoom not found"));

        // Aggiornamento Grade Scale (obbligatoria)
        if (scaleIds.containsKey("GRADE")) {
            yearRoom.setGradeScale(indicatorScaleRepository.findById(scaleIds.get("GRADE"))
                    .orElseThrow(() -> new RuntimeException("Grade Scale not found")));
        }

        // Aggiornamento Division Scale (obbligatoria)
        if (scaleIds.containsKey("DIVISION")) {
            yearRoom.setDivisionScale(indicatorScaleRepository.findById(scaleIds.get("DIVISION"))
                    .orElseThrow(() -> new RuntimeException("Division Scale not found")));
        }

        // Aggiornamento Conduct Alpha (opzionale)
        if (scaleIds.get("CONDUCT_ALPHA") != null) {
            yearRoom.setConductAlphaScale(indicatorScaleRepository.findById(scaleIds.get("CONDUCT_ALPHA")).orElse(null));
        }

        // Aggiornamento Conduct Text (opzionale)
        if (scaleIds.get("CONDUCT_TEXT") != null) {
            yearRoom.setConductTextScale(indicatorScaleRepository.findById(scaleIds.get("CONDUCT_TEXT")).orElse(null));
        }

        yearRoomRepository.save(yearRoom);
    }








    /*************************************************************************************************** 
    SUBJECTS
    ****************************************************************************************************/

    /**
     * Recupera tutte le materie per la gestione amministrativa
     */
    @Transactional(readOnly = true)
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAllByOrderBySubjectNameEngAsc();
    }

    /**
     * Crea una nuova materia
     */
    @Transactional
    public Subject createSubject(Subject subject) {
        // Possiamo aggiungere logiche di validazione qui (es. check se esiste già l'abbreviazione)
        subject.setSubjectIsActive(true); // Default attiva per le nuove
        return subjectRepository.save(subject);
    }

    /**
     * Aggiorna una materia esistente (inclusa l'attivazione/disattivazione)
     */
    @Transactional
    public Subject updateSubject(Short id, Subject subjectDetails) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + id));

        // Aggiornamento campi
        subject.setSubjectNameEng(subjectDetails.getSubjectNameEng());
        subject.setSubjectNameKsw(subjectDetails.getSubjectNameKsw());
        subject.setSubjectAbbr(subjectDetails.getSubjectAbbr());
        subject.setSubjectDescription(subjectDetails.getSubjectDescription());
        subject.setSubjectIsActive(subjectDetails.isSubjectIsActive());

        return subjectRepository.save(subject);
    }

    /**
     * Toggle rapido dello stato attivo (utile per il primo click nel modale)
     * Se la materia è attiva, la disattiva e viceversa.
     */
    @Transactional
    public void toggleSubjectStatus(Short id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        subject.setSubjectIsActive(!subject.isSubjectIsActive());
        subjectRepository.save(subject);
    }




}