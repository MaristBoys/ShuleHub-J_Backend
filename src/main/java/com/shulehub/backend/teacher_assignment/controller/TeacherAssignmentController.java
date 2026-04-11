package com.shulehub.backend.teacher_assignment.controller;

import com.shulehub.backend.common.response.ApiResponse;
import com.shulehub.backend.school_config.model.dto.YearRoomDetailDTO;
import com.shulehub.backend.subject.model.entity.Subject;
import com.shulehub.backend.teacher_assignment.model.dto.ClassTeacherSelectionDTO;
import com.shulehub.backend.teacher_assignment.model.dto.SubjectTeacherSelectionDTO;
import com.shulehub.backend.teacher_assignment.service.TeacherAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/teacher-assignments")
@RequiredArgsConstructor
public class TeacherAssignmentController {

    private final TeacherAssignmentService assignmentService;

    /**
     * Recupera la lista di tutti i docenti attivi per la scelta del Class Teacher.
     */
    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'ALL_VIEW', 'CONFIG_VIEW_ROOM')")
    @GetMapping("/eligible-class-teachers")
    public ResponseEntity<ApiResponse<List<ClassTeacherSelectionDTO>>> getEligibleClassTeachers() {
        List<ClassTeacherSelectionDTO> teachers = assignmentService.getEligibleClassTeachers();
        return ResponseEntity.ok(new ApiResponse<>(true, "Lista docenti idonei recuperata", teachers));
    }

    /**
     * Recupera la lista dei docenti abilitati a insegnare una specifica materia.
     */
    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'ALL_VIEW', 'CONFIG_VIEW_ROOM')")
    @GetMapping("/eligible-teachers")
    public ResponseEntity<ApiResponse<List<SubjectTeacherSelectionDTO>>> getEligibleTeachersForSubject(
            @RequestParam Short subjectId) {
        List<SubjectTeacherSelectionDTO> teachers = assignmentService.getEligibleTeachersForSubject(subjectId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lista docenti abilitati recuperata", teachers));
    }

    /**
     * Assegna o cambia il Class Teacher per una YearRoom.
     * Usiamo PATCH perché stiamo modificando solo una parte specifica della configurazione della stanza.
     */
    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'CONFIG_EDIT_ROOM')")
    @PatchMapping("/year-rooms/{yearRoomId}/class-teacher")
    public ResponseEntity<ApiResponse<Void>> assignClassTeacher(
            @PathVariable Integer yearRoomId,
            @RequestBody TeacherSelectionRequest request) {
        
        assignmentService.assignClassTeacher(yearRoomId, request.employeeId());
        return ResponseEntity.ok(new ApiResponse<>(true, "Class Teacher assegnato con successo", null));
    }

    /**
     * Assegna un docente a una materia specifica (Staffing) in una YearRoom.
     * Cambiamo da PUT a PATCH per coerenza con il Class Teacher e per evitare blocchi firewall/security.
     */
    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'CONFIG_EDIT_ROOM')")
    @PatchMapping("/year-rooms/{yearRoomId}/subjects/{subjectId}") // Passato a PATCH
    public ResponseEntity<ApiResponse<Void>> assignSubjectTeacher(
            @PathVariable Integer yearRoomId,
            @PathVariable Short subjectId,
            @RequestBody TeacherSelectionRequest request) {
        
        // Passiamo l'employeeId (che può essere null per l'unassign) al service
        assignmentService.assignSubjectTeacher(yearRoomId, subjectId, request.employeeId());
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Staff assignment updated successfully", null));
    }

    /**
     * DTO interno per ricevere l'ID dell'impiegato dal frontend.
     */
    public record TeacherSelectionRequest(UUID employeeId) {}


    /**
     * Attiva o disattiva un'assegnazione specifica.
     * PATCH /api/v1/teacher-assignments/staffing/{assignmentId}/status?active=true
    
    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'CONFIG_EDIT_ROOM')")
    @PatchMapping("/staffing/{assignmentId}/status")
    public ResponseEntity<ApiResponse<Void>> toggleAssignmentStatus(
            @PathVariable Integer assignmentId,
            @RequestParam boolean active) {
        
        assignmentService.toggleAssignmentStatus(assignmentId, active);
        String message = active ? "Subject activated" : "Subject deactivated";
        return ResponseEntity.ok(new ApiResponse<>(true, message, null));
    }
 */
    /**
     * Attiva o disattiva un'assegnazione specifica usando YearRoom e Subject.
     */
    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'CONFIG_EDIT_ROOM')")
    @PatchMapping("/year-rooms/{yearRoomId}/subjects/{subjectId}/toggle")
    public ResponseEntity<ApiResponse<Void>> toggleAssignmentStatus(
            @PathVariable Integer yearRoomId,
            @PathVariable Short subjectId) {
        
        // Il service deve trovare il record e invertire il flag active
        boolean newState = assignmentService.toggleAssignmentStatus(yearRoomId, subjectId);
        
        String message = newState ? "Subject activated" : "Subject deactivated";
        return ResponseEntity.ok(new ApiResponse<>(true, message, null));
    }



    /**
     * Rimuove un'assegnazione. 
     * Se ci sono voti, il backend risponderà con successo ma il record sarà solo disattivato.
     */
    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'CONFIG_EDIT_ROOM')")
    @DeleteMapping("/staffing/{assignmentId}")
    public ResponseEntity<ApiResponse<Boolean>> removeAssignment(@PathVariable Integer assignmentId) {
        boolean isSoftDeleted = assignmentService.removeAssignment(assignmentId);
        
        String message = isSoftDeleted 
            ? "Subject archived (contains grades, set to inactive)" 
            : "Subject removed successfully";
            
        return ResponseEntity.ok(new ApiResponse<>(true, message, isSoftDeleted));
    }


    /**
     * Recupera le materie disponibili per l'aggiunta in una specifica stanza.
     */
    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'CONFIG_VIEW_ROOM')")
    @GetMapping("/year-rooms/{yearRoomId}/available-subjects")
    public ResponseEntity<ApiResponse<List<Subject>>> getAvailableSubjects(@PathVariable Integer yearRoomId) {
        List<Subject> subjects = assignmentService.getAvailableSubjectsForRoom(yearRoomId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Materie disponibili recuperate", subjects));
    }

    /**
     * Aggiunge una materia alla stanza.
     */
    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'CONFIG_EDIT_ROOM')")
    @PostMapping("/year-rooms/{yearRoomId}/subjects/{subjectId}")
    public ResponseEntity<ApiResponse<Void>> addSubjectToRoom(
            @PathVariable Integer yearRoomId, 
            @PathVariable Short subjectId) {
        assignmentService.addSubjectToRoom(yearRoomId, subjectId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Materia aggiunta alla stanza con successo", null));
    }


    /**
     * DTO per la richiesta di copia massiva della configurazione (Smart Copy).
     * * @param previousYearId L'ID dell'anno scolastico da cui prelevare i dati (Sorgente).
     * @param copyTeachers   Se true, il sistema tenterà di mantenere gli stessi docenti
     * anche nel nuovo anno (previa verifica dello stato attivo).
     * @param copyClassTeacher Se true, clona anche il record relativo al coordinatore di classe se attivo.
     */
    // Questo record serve a mappare i dati inviati dal frontend durante l'operazione di "Smart Copy". 
    // Lo abbiamo aggiunto per trasportare in un unico oggetto i parametri che guidano la logica di clonazione.
    public record SmartCopyRequest(
        Integer sourceYearRoomId, // Cambiato da Short previousYearId
        boolean copyTeachers, 
        boolean copyClassTeacher
    ) {}


    /**
     * Esegue la copia massiva della configurazione da un anno precedente.
     */
/*    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'CONFIG_EDIT_ROOM')")
    @PostMapping("/year-rooms/{yearRoomId}/smart-copy")
    public ResponseEntity<ApiResponse<Void>> smartCopy(
            @PathVariable Integer yearRoomId,
            @RequestBody SmartCopyRequest request) {
        
        assignmentService.smartCopyFromPreviousYear(yearRoomId, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Configurazione copiata con successo", null));
    }
*/
    

    @PreAuthorize("hasAnyAuthority('ALL_ACCESS', 'ALL_VIEW', 'CONFIG_VIEW_ROOM')")
    @GetMapping("/year-rooms/{yearRoomId}/eligible-sources")
    public ResponseEntity<ApiResponse<List<YearRoomDetailDTO>>> getEligibleSourceRooms(
            @PathVariable Integer yearRoomId) {
        List<YearRoomDetailDTO> sources = assignmentService.getEligibleSourceRooms(yearRoomId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Source rooms retrieved", sources));
    }


}