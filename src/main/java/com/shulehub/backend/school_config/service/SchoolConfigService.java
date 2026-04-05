package com.shulehub.backend.school_config.service;

// Import dei DTO e delle View specifiche per la configurazione
import com.shulehub.backend.school_config.model.dto.*;
import com.shulehub.backend.school_config.model.view.YearRoomDetailView;
import com.shulehub.backend.school_config.model.view.YearRoomStatsView;
import com.shulehub.backend.school_config.repository.YearRoomDetailViewRepository;
import com.shulehub.backend.school_config.repository.YearRoomStatsViewRepository;
import com.shulehub.backend.school_config.repository.YearRoomStudentRepository;
import com.shulehub.backend.school_structure.model.entity.Form;
import com.shulehub.backend.school_structure.model.entity.Room;
import com.shulehub.backend.school_structure.model.entity.Year;
import com.shulehub.backend.school_structure.model.entity.YearRoom;
import com.shulehub.backend.school_structure.repository.RoomRepository;
import com.shulehub.backend.school_structure.repository.YearRoomRepository;
// Import dei service specialistici per delegare la logica di dominio
import com.shulehub.backend.indicator_scale.service.IndicatorScaleService;
import com.shulehub.backend.school_structure.service.SchoolStructureService;
import com.shulehub.backend.subject.service.SubjectService;
import com.shulehub.backend.teacher_assignment.repository.TeacherAssignmentRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SchoolConfigService {

    // REPOSITORY LOCALI
    private final YearRoomDetailViewRepository yearRoomDetailViewRepository;
    private final YearRoomStatsViewRepository yearRoomStatsViewRepository;
    private final YearRoomStudentRepository yearRoomStudentRepository;
    
    
    // REPOSITORY ESTERNI (bisognorebbe passare dal service)
    private final YearRoomRepository yearRoomRepository;
    private final RoomRepository roomRepository;
    private final TeacherAssignmentRepository teacherAssignmentRepository; 

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

        // Recuperiamo i due conteggi distinti tramite i nuovi metodi del service
        long activeRooms = schoolStructureService.countActiveYearRoomsByYearId(activeYear.getId());
        long totalRooms = schoolStructureService.countTotalYearRoomsByYearId(activeYear.getId());
        long activeSubjects = subjectService.countActiveSubjects();

        return new SchoolConfigSummaryDTO(
                activeYear.getId(),
                activeYear.getYear(),
                activeRooms,    // Campo per le stanze con yearRoomIsActive = true
                totalRooms,     // Nuovo campo per il totale delle stanze configurate
                activeSubjects
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
                        true, // isAssigned, la room esiste e non è una ghost cell
                        stats != null ? stats.isYearroomIsActive() : true,
                        stats != null ? stats.getStudentCount() : 0,
                        stats != null ? stats.getClassTeacherId() : null,
                        stats != null ? stats.getClassTeacherName() : "No CT assigned",
                        ratio,
                        percentage
                    ));
                } else {
                    // Cella vuota: il frontend mostrerà il tasto "+"
                    cells.put(sNum, new YearRoomSummaryDTO(null, null, false,false, 0, null, null, null, 0.0));
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
     * Gestisce sia stanze esistenti che "Ghost Cells" (nuove attivazioni).
     */
    @Transactional(readOnly = true)
    public YearRoomDetailDTO getYearRoomDetails(Integer yearRoomId, Short roomNum, Short yearId) {
        
        YearRoomDetailDTO.SelectedScales currentScales;
        YearRoomDetailDTO.YearRoomDetailDTOBuilder dtoBuilder = YearRoomDetailDTO.builder();

        if (yearRoomId != null) {
            // --- CASO A: STANZA ESISTENTE ---
            YearRoom yrEntity = schoolStructureService.getYearRoomById(yearRoomId);
            
            YearRoomDetailView detailView = yearRoomDetailViewRepository.findByYearRoomId(yearRoomId)
                    .orElseThrow(() -> new RuntimeException("YearRoom details not found for ID: " + yearRoomId));
            
            YearRoomStatsView statsView = yearRoomStatsViewRepository.findById(yearRoomId)
                    .orElse(new YearRoomStatsView());

            // Popolamento Scale Effettive dal Database (tramite View)
            currentScales = YearRoomDetailDTO.SelectedScales.builder()
                    .gradeScaleId(detailView.getGradeScaleId())
                    .gradeScaleName(detailView.getGradeScaleName())
                    .divisionScaleId(detailView.getDivisionScaleId())
                    .divisionScaleName(detailView.getDivisionScaleName())
                    .conductAlphaScaleId(detailView.getConductAlphaScaleId())
                    .conductAlphaScaleName(detailView.getConductAlphaScaleName())
                    .conductTextScaleId(detailView.getConductTextScaleId())
                    .conductTextScaleName(detailView.getConductTextScaleName())
                    .build();

            // Dati Header e liste
            dtoBuilder.yearRoomId(yearRoomId)
                      .roomId(yrEntity.getRoom().getId())
                      .roomName(detailView.getRoomName())
                      .formName(detailView.getFormName())
                      .yearName(yrEntity.getYear().getYearDescription())
                      .isActive(yrEntity.getYearRoomIsActive())
                      .studentCount(statsView.getStudentCount())
                      .classTeacherName(statsView.getClassTeacherName())
                      .staffingRatio(statsView.getAssignedSubjects() + "/" + statsView.getTotalSubjects())
                      .staffAssignments(getStaffAssignments(yearRoomId))
                      .enrolledStudents(getEnrolledStudents(yearRoomId));
        } 
        else {
            // --- CASO B: GHOST CELL (NUOVA STANZA) ---
            // Recuperiamo le entità di base per popolare l'header del modale
            Room room = schoolStructureService.getRoomByNum(roomNum); // CAMBIATO per cercare la stanza tramite numero
            Year year = schoolStructureService.getYearById(yearId);

            // LOGICA SMART DEFAULT:
            // Chiamiamo il metodo che cerca le scale suggerite in base al range FormFrom/To
            // Il metodo restituisce già l'oggetto SelectedScales con ID e Nomi popolati.
            currentScales = indicatorScaleService.getSuggestedScalesByForm(room.getForm().getFormNum());

            dtoBuilder.yearRoomId(null)
                      .roomId(room.getId())
                      .roomName(room.getRoomName())
                      .formName(room.getForm().getFormName())
                      .yearName(year.getYearDescription())
                      .isActive(false)
                      .studentCount(0)
                      .classTeacherName("Not Assigned")
                      .staffingRatio("0/0")
                      .staffAssignments(Collections.emptyList())
                      .enrolledStudents(Collections.emptyList());
        }

        return dtoBuilder.currentScales(currentScales).build();
    }

    // --- METODI PRIVATI DI SUPPORTO PER LA PULIZIA DEL CODICE ---

    private List<YearRoomDetailDTO.StaffAssignmentInfo> getStaffAssignments(Integer yearRoomId) {
        return teacherAssignmentRepository.findByYearRoomId(yearRoomId)
                .stream()
                .map(ta -> {
                    var builder = YearRoomDetailDTO.StaffAssignmentInfo.builder()
                            .isClassTeacher(ta.isClassTeacher());

                    // Gestione Subject
                    if (ta.getSubject() != null) {
                        builder.subjectId(ta.getSubject().getId())
                               .subjectName(ta.getSubject().getSubjectNameEng());
                    } else {
                        builder.subjectId(null)
                               .subjectName("No Subject");
                    }

                    // Gestione Employee (Docente)
                    if (ta.getEmployee() != null) {
                        builder.teacherId(ta.getEmployee().getId())
                               .fullName(ta.getEmployee().getPerson() != null 
                                       ? ta.getEmployee().getPerson().getFullName() 
                                       : "Unknown Name")
                               .isActive(ta.getEmployee().isEmployeeIsActive());
                    } else {
                        builder.teacherId(null)
                               .fullName("Not Assigned")
                               .isActive(false);
                    }

                    return builder.build();
                })
                .collect(Collectors.toList());
    }

    private List<YearRoomDetailDTO.StudentListItemDTO> getEnrolledStudents(Integer yearRoomId) {
        return yearRoomStudentRepository.findByYearRoomId(yearRoomId)
                .stream()
                .map(yrs -> YearRoomDetailDTO.StudentListItemDTO.builder()
                        .studentId(yrs.getStudent().getId())
                        .fullName(yrs.getStudent().getPerson().getFullName())
                        .isActive(yrs.getStudent().isStudentIsActive())
                        .build())
                .collect(Collectors.toList());
    }

   
    /**
     * Aggiorna lo stato della YearRoom per abilitare o disabilitare
     * Questo metodo coordina l'aggiornamento dell'entità YearRoom.
     */
    @Transactional
    public void updateRoomStatus(Integer yearRoomId, Boolean active) {
        YearRoom yr = yearRoomRepository.findById(yearRoomId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        yr.setYearRoomIsActive(active);
        // yearRoomRepository.save(yr); // Opzionale con @Transactional
    }
    
    /*
     * Metodo per assegnare una stanza a un anno (creazione YearRoom) con le scale di valutazione
     * Questo metodo coordina la creazione dell'entità YearRoom e l'assegnazione delle scale tramite IndicatorScaleService.
    */
/*  @Transactional
    public YearRoom assignRoom(Short roomNum, Short yearId, Boolean isActive, Map<String, Object> scaleData) {
        // 1. Recuperiamo le entità fisiche tramite lo SchoolStructureService
        Room room = schoolStructureService.getRoomByNum(roomNum);
        Year year = schoolStructureService.getYearById(yearId);

        // 2. Creiamo la nuova configurazione YearRoom
        YearRoom yearRoom = new YearRoom();
        yearRoom.setRoom(room);
        yearRoom.setYear(year);
        yearRoom.setYearRoomIsActive(isActive != null ? isActive : true);

        // 3. Settiamo le scale usando i metodi che abbiamo già per l'update
        // Nota: dobbiamo convertire i valori della mappa in Short
        if (scaleData.containsKey("GRADE")) {
            yearRoom.setGradeScale(indicatorScaleService.getScaleById(((Number) scaleData.get("GRADE")).shortValue()));
        }
        if (scaleData.containsKey("DIVISION")) {
            yearRoom.setDivisionScale(indicatorScaleService.getScaleById(((Number) scaleData.get("DIVISION")).shortValue()));
        }
        if (scaleData.containsKey("CONDUCT_ALPHA")) {
            yearRoom.setConductAlphaScale(indicatorScaleService.getScaleById(((Number) scaleData.get("CONDUCT_ALPHA")).shortValue()));
        }
        if (scaleData.containsKey("CONDUCT_TEXT")) {
            yearRoom.setConductTextScale(indicatorScaleService.getScaleById(((Number) scaleData.get("CONDUCT_TEXT")).shortValue()));
        }

        // 4. Salviamo tramite il service della struttura che ha già il repository
        return schoolStructureService.saveYearRoom(yearRoom);
    }
*/
    @Transactional
    public YearRoom assignRoom(Short roomNum, Short yearId, Boolean isActive, Map<String, Object> scaleData) {
        Room room = schoolStructureService.getRoomByNum(roomNum);
        Year year = schoolStructureService.getYearById(yearId);

        YearRoom yearRoom = new YearRoom();
        yearRoom.setRoom(room);
        yearRoom.setYear(year);
        yearRoom.setYearRoomIsActive(isActive);

        // Conversione sicura per ogni scala
        if (scaleData.get("GRADE") != null) {
            yearRoom.setGradeScale(indicatorScaleService.getScaleById(
                Short.valueOf(scaleData.get("GRADE").toString())
            ));
        }
        if (scaleData.get("DIVISION") != null) {
            yearRoom.setDivisionScale(indicatorScaleService.getScaleById(
                Short.valueOf(scaleData.get("DIVISION").toString())
            ));
        }
        if (scaleData.get("CONDUCT_ALPHA") != null) {
            yearRoom.setConductAlphaScale(indicatorScaleService.getScaleById(
                Short.valueOf(scaleData.get("CONDUCT_ALPHA").toString())
            ));
        }
        if (scaleData.get("CONDUCT_TEXT") != null) {
            yearRoom.setConductTextScale(indicatorScaleService.getScaleById(
                Short.valueOf(scaleData.get("CONDUCT_TEXT").toString())
            ));
        }

        return schoolStructureService.saveYearRoom(yearRoom);
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
  