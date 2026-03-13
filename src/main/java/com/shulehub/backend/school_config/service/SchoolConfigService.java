package com.shulehub.backend.school_config.service;

// Import dei DTO e delle View specifiche per la configurazione
import com.shulehub.backend.school_config.model.dto.*;
import com.shulehub.backend.school_config.model.view.YearRoomDetailView;
import com.shulehub.backend.school_config.model.view.YearRoomStatsView;
import com.shulehub.backend.school_config.repository.YearRoomDetailViewRepository;
import com.shulehub.backend.school_config.repository.YearRoomStatsViewRepository;
import com.shulehub.backend.school_config.repository.YearRoomStudentRepository;
import com.shulehub.backend.school_structure.model.entity.Form;
import com.shulehub.backend.school_structure.model.entity.Year;
import com.shulehub.backend.school_structure.model.entity.YearRoom;

// Import dei service specialistici per delegare la logica di dominio
import com.shulehub.backend.indicator_scale.service.IndicatorScaleService;
import com.shulehub.backend.school_structure.service.SchoolStructureService;
import com.shulehub.backend.subject.service.SubjectService;
import com.shulehub.backend.teacher_assignment.repository.TeacherAssignmentRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SchoolConfigService {

    // REPOSITORY LOCALI
    //private final YearRoomRepository yearRoomRepository;
    private final YearRoomDetailViewRepository yearRoomDetailViewRepository;
    private final YearRoomStatsViewRepository yearRoomStatsViewRepository;
    private final TeacherAssignmentRepository teacherAssignmentRepository; // Assicurati che esista
    private final YearRoomStudentRepository yearRoomStudentRepository;     // Assicurati che esista

    // SERVICE ESTERNI (Orchestrazione)
    private final IndicatorScaleService indicatorScaleService;
    private final SchoolStructureService schoolStructureService;
    private final SubjectService subjectService;

    /*************************************************************************************************** 
    Dati inseriti nella card di configurazione generale (Anni, Materie, Stanze attive)
    ****************************************************************************************************/

     /**
     * Recupera un riassunto generale della configurazione per la dashboard.
     * Combina dati da structure (Anni), subject (Materie) e config (Stanze attive).
     */
    @Transactional(readOnly = true)
    public SchoolConfigSummaryDTO getSchoolConfigSummary() {
        Year activeYear = schoolStructureService.getActiveYear(); // Recupera l'anno attivo tramite il service dedicato

        return new SchoolConfigSummaryDTO(
                activeYear.getId(),
                activeYear.getYear(),
                schoolStructureService.countYearRoomsByYearId(activeYear.getId()), 
                subjectService.countActiveSubjects()
        );
    }

    /*************************************************************************************************** 
    YEARS (prima riga della card) - Gestiti da SchoolStructureService ma con endpoint in SchoolConfigController per coerenza di UX
    ****************************************************************************************************/
    /*************************************************************************************************** 
    SUBJECTS (terza riga card) - gestiti da SubjectService (ma con endpoint in SchoolConfigController per coerenza di UX)
    ****************************************************************************************************/

    /*************************************************************************************************** 
    ROOMS (seconda riga card) - gestiti da SchoolConfigService (perché coinvolgono logica complessa e DTO specifici)
    ****************************************************************************************************/
    
    /*************************************************************************************************** 
        ROOMS - costruzione matrice con le singole card per ciascuna room
    ****************************************************************************************************/
    /**
     * Costruisce la matrice delle classi (Griglia 6 form x 3 stream).
     * Questa logica rimane qui perché è strettamente legata alla visualizzazione della UI 
     * di configurazione e utilizza una View (YearRoomStatsView) aggregata.
     */
    @Transactional(readOnly = true)
    public RoomMatrixDTO getRoomMatrix(Short yearId) {
        // 1. Carichiamo le stanze e le statistiche aggregate in "Bulk"
           //Caricamento dati tramite Service specialisti e Repo locale
        //Year year = schoolStructureService.getYearById(yearId); non ustato
        List<Form> allForms = schoolStructureService.getActiveFormsSorted(); // UNICO punto di accesso per i Form
        
        List<YearRoom> yearRooms = schoolStructureService.getYearRoomsByYearId(yearId); // CAMBIATO per usare il service
        List<YearRoomStatsView> allStats = yearRoomStatsViewRepository.findByYearId(yearId);
        //List<Form> forms = schoolStructureService.getAllForms();
 

        // 2. Trasformiamo le statistiche in mappa (Key: yearRoomId)
        Map<Integer, YearRoomStatsView> statsMap = allStats.stream()
            .collect(Collectors.toMap(YearRoomStatsView::getYearRoomId, s -> s, (existing, replacement) -> existing));

        // 3. Mappa per posizionare le stanze nella griglia (Key: formId-streamNum)
        Map<String, YearRoom> roomMap = new HashMap<>();
        for (YearRoom yr : yearRooms) {
            if (yr.getRoom() != null && yr.getRoom().getForm() != null) {
                // Estraiamo lo stream dall'ultima cifra del numero stanza (es. 21 -> 1)
                int streamNum = yr.getRoom().getRoomNum() % 10; 
                // Usiamo l'ID del Form come coordinata per la riga
                String key = yr.getRoom().getForm().getId() + "-" + streamNum;
                roomMap.put(key, yr);
            }
        }

        // 4. Definiamo le colonne (Stream 1, 2, 3) e le righe (Form attivi)
        List<Short> streams = List.of((short)1, (short)2, (short)3);

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

    /*************************************************************************************************** 
        ROOMS - costruzione modale di dettaglio per ciascuna room (YearRoomDetailDTO)
        Tab Info & Scales - sezione con i dettagli generali della stanza e le scale di valutazione (calcolate da IndicatorScaleService) 
    ****************************************************************************************************/
    /**
     * Recupera i dettagli completi per il modale di configurazione di una stanza.
     * non Delega a IndicatorScaleService il calcolo delle scale suggerite.
     */
    @Transactional(readOnly = true)
    public YearRoomDetailDTO getYearRoomDetails(Integer yearRoomId) {
        // 1. Recupero l'entità YearRoom per avere accesso a Year e Room direttamente
        // Usiamo il service della struttura che hai già
        YearRoom yrEntity = schoolStructureService.getYearRoomById(yearRoomId);
        
        
        // 1. Recupero i dati base e le scale dalla View di dettaglio
        YearRoomDetailView detailView = yearRoomDetailViewRepository.findById(yearRoomId)
                .orElseThrow(() -> new RuntimeException("YearRoom details not found for ID: " + yearRoomId));

        // 2. Recupero i dati di sintesi (Header) dalla View Stats
        YearRoomStatsView statsView = yearRoomStatsViewRepository.findById(yearRoomId)
                .orElse(new YearRoomStatsView()); // Fallback se non ancora calcolate

        // 3. Recupero la lista dei docenti assegnati (Staffing)
        List<YearRoomDetailDTO.StaffAssignmentInfo> staff = teacherAssignmentRepository.findByYearRoomId(yearRoomId)
                .stream()
                .map(ta -> YearRoomDetailDTO.StaffAssignmentInfo.builder()
                        .subjectId(ta.getSubject().getId())
                        .subjectName(ta.getSubject().getSubjectNameEng()) // o Ksw
                        .teacherId(ta.getEmployee().getId())
                        .fullName(ta.getEmployee().getPerson().getFullName())
                        .isClassTeacher(ta.isClassTeacher())
                        .isActive(ta.getEmployee().isEmployeeIsActive())
                        .build())
                .collect(Collectors.toList());

        // 4. Recupero la lista degli studenti (Enrollment)
        List<YearRoomDetailDTO.StudentListItemDTO> students = yearRoomStudentRepository.findByYearRoomId(yearRoomId)
                .stream()
                .map(yrs -> YearRoomDetailDTO.StudentListItemDTO.builder()
                        .studentId(yrs.getStudent().getId())
                        .fullName(yrs.getStudent().getPerson().getFullName())
                        .isActive(yrs.getStudent().isStudentIsActive())
                        .build())
                .collect(Collectors.toList());

        // 5. Costruzione dei suggerimenti per le scale (logica che avevi già)
        Map<String, Short> suggested = getSuggestedScalesForForm(detailView.getFormId());

        // 6. Assemblaggio finale del DTO
        return YearRoomDetailDTO.builder()
                .yearRoomId(detailView.getYearRoomId())
                .roomName(detailView.getRoomName())
                .formName(detailView.getFormName())
                .yearName(yrEntity.getYear().getYearDescription())
                .studentCount(statsView.getStudentCount())
                .classTeacherName(statsView.getClassTeacherName())
                .staffingRatio(statsView.getAssignedSubjects() + "/" + statsView.getTotalSubjects())
                .currentScales(YearRoomDetailDTO.SelectedScales.builder()
                        .gradeScaleId(detailView.getGradeScaleId())
                        .gradeScaleName(detailView.getGradeScaleName())
                        .divisionScaleId(detailView.getDivisionScaleId())
                        .divisionScaleName(detailView.getDivisionScaleName())
                        .conductAlphaScaleId(detailView.getConductAlphaScaleId())
                        .conductAlphaScaleName(detailView.getConductAlphaScaleName())
                        .conductTextScaleId(detailView.getConductTextScaleId())
                        .conductTextScaleName(detailView.getConductTextScaleName())
                        .build())
                .suggestedScaleIds(suggested)
                .staffAssignments(staff)
                .enrolledStudents(students)
                .build();
    }


    /**
     * Logica di business per suggerire le scale in base al Form.
     * Questa mappa viene usata dal frontend per pre-selezionare i valori nei dropdown.
     */
    private Map<String, Short> getSuggestedScalesForForm(Short formId) {
        Map<String, Short> suggestions = new HashMap<>();
        
        // Esempio di logica: 
        // Se Form < 5 (Primary), suggeriamo certi ID, altrimenti altri (Secondary)
        if (formId != null && formId <= 4) {
            suggestions.put("GRADE", (short) 1);         // ID della scala voti Primary
            suggestions.put("DIVISION", (short) 3);      // ID della scala divisioni Primary
            suggestions.put("CONDUCT_ALPHA", (short) 5); 
            suggestions.put("CONDUCT_TEXT", (short) 6);
        } else {
            suggestions.put("GRADE", (short) 2);         // ID della scala voti Secondary
            suggestions.put("DIVISION", (short) 4);
            suggestions.put("CONDUCT_ALPHA", (short) 5);
            suggestions.put("CONDUCT_TEXT", (short) 6);
        }
        
        return suggestions;
    }



    /**
     * Aggiorna le scale di valutazione per una stanza.
     * Questo metodo coordina l'aggiornamento dell'entità YearRoom.
     */
    @Transactional
    public void updateYearRoomScales(Integer yearRoomId, Map<String, Short> scaleIds) {
        // 1. Recupero dell'entità locale (YearRoom appartiene a questo modulo)
        YearRoom yearRoom = schoolStructureService.getYearRoomById(yearRoomId); // CAMBIATO per usare il service
        
        // 2. Aggiornamento Grade Scale tramite Service esterno
        if (scaleIds.containsKey("GRADE")) {
            yearRoom.setGradeScale(indicatorScaleService.getScaleById(scaleIds.get("GRADE")));
        }

        // 3. Aggiornamento Division Scale tramite Service esterno
        if (scaleIds.containsKey("DIVISION")) {
            yearRoom.setDivisionScale(indicatorScaleService.getScaleById(scaleIds.get("DIVISION")));
        }

        // 4. Aggiornamento Conduct Alpha (opzionale)
        if (scaleIds.containsKey("CONDUCT_ALPHA")) {
            Short id = scaleIds.get("CONDUCT_ALPHA");
            // Se l'ID è nullo, resettiamo la scala, altrimenti la cerchiamo tramite service
            yearRoom.setConductAlphaScale(id != null ? indicatorScaleService.getScaleById(id) : null);
        }

        // 5. Aggiornamento Conduct Text (opzionale)
        if (scaleIds.containsKey("CONDUCT_TEXT")) {
            Short id = scaleIds.get("CONDUCT_TEXT");
            yearRoom.setConductTextScale(id != null ? indicatorScaleService.getScaleById(id) : null);
        }

        // 6. Salvataggio finale dell'entità orchestrata
        schoolStructureService.saveYearRoom(yearRoom); // CAMBIATO per usare il service
    }
    
    
    /*************************************************************************************************** 
        ROOMS - costruzione modale di dettaglio per ciascuna room (YearRoomDetailDTO)
        Tab Teachers - sezione per assegnare il docente alla stanza (da implementare) 
    ****************************************************************************************************/

     /*************************************************************************************************** 
        ROOMS - costruzione modale di dettaglio per ciascuna room (YearRoomDetailDTO)
        Tab Students - sezione per assegnare gli studenti alla stanza (da implementare) 
    ****************************************************************************************************/
    
}
  