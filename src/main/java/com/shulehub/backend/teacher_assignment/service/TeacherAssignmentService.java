package com.shulehub.backend.teacher_assignment.service;

import com.shulehub.backend.registry.model.entity.Employee;
import com.shulehub.backend.registry.repository.EmployeeRepository;
import com.shulehub.backend.school_config.model.dto.YearRoomDetailDTO;
import com.shulehub.backend.school_structure.model.entity.YearRoom;
import com.shulehub.backend.school_structure.repository.YearRoomRepository;
import com.shulehub.backend.subject.model.entity.Subject;
import com.shulehub.backend.subject.repository.SubjectRepository;
import com.shulehub.backend.teacher_assignment.controller.TeacherAssignmentController.SmartCopyRequest;
import com.shulehub.backend.teacher_assignment.model.dto.ClassTeacherSelectionDTO;
import com.shulehub.backend.teacher_assignment.model.dto.SubjectTeacherSelectionDTO;
import com.shulehub.backend.teacher_assignment.model.entity.TeacherAssignment;
import com.shulehub.backend.teacher_assignment.repository.TeacherAssignmentRepository;
import com.shulehub.backend.school_config.model.dto.YearRoomDetailDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherAssignmentService {

    private final TeacherAssignmentRepository assignmentRepository;
    private final EmployeeRepository employeeRepository;
    private final YearRoomRepository yearRoomRepository;
    private final SubjectRepository subjectRepository;

    /**
     * Recupera la lista di docenti idonei per il ruolo di Class Teacher.
     */
    @Transactional(readOnly = true)
    public List<ClassTeacherSelectionDTO> getEligibleClassTeachers() {
        return employeeRepository.findAllActiveForClassTeacherSelection();
    }

    /**
     * Recupera la lista di docenti idonei per insegnare una specifica materia.
     */
    @Transactional(readOnly = true)
    public List<SubjectTeacherSelectionDTO> getEligibleTeachersForSubject(Short subjectId) {
        return employeeRepository.findActiveBySubjectId(subjectId);
    }

    /**
     * Assegna o aggiorna il Class Teacher di una stanza.
     * prevede anche il caso di svuotarlo
     * Logica: Cerca se esiste già un record con subject=null, se sì lo aggiorna, altrimenti lo crea.
     */
    @Transactional
    public void assignClassTeacher(Integer yearRoomId, UUID employeeId) {
        // 1. Cerchiamo se esiste già un'assegnazione Class Teacher per questa stanza
        // Usiamo il metodo già presente nel repository
        Optional<TeacherAssignment> existingAssignment = assignmentRepository
                .findByYearRoomIdAndSubjectIsNullAndClassTeacherTrue(yearRoomId);

        // 2. CASO RIMOZIONE: Se l'ID dell'impiegato è null, procediamo al delete
        if (employeeId == null) {
            existingAssignment.ifPresent(assignment -> {
                // Utilizziamo il metodo delete predefinito di JpaRepository
                assignmentRepository.delete(assignment);
            });
            return; 
        }

        // 3. CASO ASSEGNAZIONE O UPDATE:
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));
        
        // Se l'assegnazione esiste già, la aggiorniamo, altrimenti ne creiamo una nuova
        TeacherAssignment assignment = existingAssignment.orElseGet(() -> {
            YearRoom yearRoom = yearRoomRepository.findById(yearRoomId)
                    .orElseThrow(() -> new RuntimeException("YearRoom not found with ID: " + yearRoomId));
            
            TeacherAssignment newAssignment = new TeacherAssignment();
            newAssignment.setYearRoom(yearRoom);
            newAssignment.setSubject(null);
            newAssignment.setClassTeacher(true);
            return newAssignment;
        });

        assignment.setEmployee(employee);
        
        // Il save gestisce sia l'inserimento del nuovo che l'update dell'esistente
        assignmentRepository.save(assignment);
    }

    /**
     * Assegna un docente a una specifica materia (Staffing).
     */
/*    @Transactional
    public void assignSubjectTeacher(Integer yearRoomId, Short subjectId, UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        YearRoom yearRoom = yearRoomRepository.findById(yearRoomId)
                .orElseThrow(() -> new RuntimeException("YearRoom not found"));
                
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        // Cerchiamo se esiste già un'assegnazione per questa materia in questa stanza
        TeacherAssignment assignment = assignmentRepository
                .findByYearRoomIdAndSubjectId(yearRoomId, subjectId)
                .orElse(new TeacherAssignment());

        if (assignment.getId() == null) {
            assignment.setYearRoom(yearRoom);
            assignment.setSubject(subject);
            assignment.setClassTeacher(false);
        }

        assignment.setEmployee(employee);
        assignmentRepository.save(assignment);
    }
*/

    @Transactional
    public void assignSubjectTeacher(Integer yearRoomId, Short subjectId, UUID employeeId) {
        // 1. Validazione preventiva: subjectId non può essere null qui
        if (subjectId == null) {
            throw new RuntimeException("subjectId obbligatorio per l'assegnazione dello Staffing");
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        // 2. Cerchiamo l'assegnazione esistente (anche se isActive = false)
        TeacherAssignment assignment = assignmentRepository
                .findByYearRoomIdAndSubjectId(yearRoomId, subjectId)
                .orElseGet(() -> {
                    // Se non esiste, creiamo un nuovo record configurando le relazioni obbligatorie
                    YearRoom yearRoom = yearRoomRepository.findById(yearRoomId)
                            .orElseThrow(() -> new RuntimeException("YearRoom not found"));
                    Subject subject = subjectRepository.findById(subjectId)
                            .orElseThrow(() -> new RuntimeException("Subject not found"));
                    
                    TeacherAssignment newTa = new TeacherAssignment();
                    newTa.setYearRoom(yearRoom);
                    newTa.setSubject(subject);
                    newTa.setClassTeacher(false); // Sicurezza: non è un class teacher
                    return newTa;
                });

        // 3. Aggiorniamo il docente
        assignment.setEmployee(employee);

        // 4. LOGICA CRITICA: Se la materia era disattivata, la riattiviamo
        // Questo permette di "recuperare" una materia archiviata semplicemente riassegnando un docente
        assignment.setActive(true);

        assignmentRepository.save(assignment);
    }

    public List<YearRoomDetailDTO.StaffAssignmentInfo> getStaffAssignmentsForRoom(Integer yearRoomId) {
        List<TeacherAssignment> assignments = assignmentRepository.findStaffingByYearRoomId(yearRoomId);

        return assignments.stream()
            .map(ta -> {
                // Subject è garantito dal filtro IS NOT NULL nella query, 
                // ma mettiamo comunque dei fallback di sicurezza
                String sName = (ta.getSubject() != null) ? ta.getSubject().getSubjectNameEng() : "Unknown Subject";
                String sAbbr = (ta.getSubject() != null) ? ta.getSubject().getSubjectAbbr() : "??";
                
                // Gestione Employee e Person (che ora possono essere NULL)
                UUID tId = (ta.getEmployee() != null) ? ta.getEmployee().getId() : null;
                
                String fName = "Not Assigned"; // Default
                boolean active = false;
                
                if (ta.getEmployee() != null) {
                    active = ta.getEmployee().isEmployeeIsActive();
                    if (ta.getEmployee().getPerson() != null) {
                        fName = ta.getEmployee().getPerson().getFullName();
                    }
                }

                // Verifichiamo se questa specifica assegnazione ha voti
                boolean hasMarks = assignmentRepository.hasPhysicalMarks(
                    ta.getSubject().getId(), 
                    ta.getYearRoom().getYear().getId()
                ) || assignmentRepository.hasConductMarks(
                    ta.getYearRoom().getId(), 
                    ta.getYearRoom().getYear().getId()
                );

                return YearRoomDetailDTO.StaffAssignmentInfo.builder()
                    .subjectId(ta.getSubject() != null ? ta.getSubject().getId() : null)
                    .subjectName(sName)
                    .subjectAbbr(sAbbr)
                    .teacherId(tId)
                    .fullName(fName)
                    .isClassTeacher(ta.isClassTeacher())
                    .isTeacherActive(active)
                    .isAssignmentActive(ta.isActive())
                    .hasStoredMarks(hasMarks)
                    .build();
            })
            .toList();
    }

    /**
     * Recupera le materie che possono essere aggiunte alla stanza.
     * Esclude quelle già presenti (anche se disattivate) e include solo quelle attive nel sistema.
     */
    @Transactional(readOnly = true)
    public List<Subject> getAvailableSubjectsForRoom(Integer yearRoomId) {
        // 1. Recuperiamo tutte le materie attive dal sistema (dal SubjectRepository)
        List<Subject> allActiveSubjects = subjectRepository.findBySubjectIsActiveTrueOrderBySubjectNameEngAsc();
        
        // 2. Recuperiamo gli ID di quelle già assegnate a questa stanza
        List<Short> assignedIds = assignmentRepository.findAssignedSubjectIds(yearRoomId);
        
        // 3. Filtriamo la lista escludendo le già presenti
        return allActiveSubjects.stream()
                .filter(s -> !assignedIds.contains(s.getId()))
                .toList();
    }

    /**
     * Aggiunge una nuova materia a una YearRoom (senza docente iniziale).
     */
    @Transactional
    public void addSubjectToRoom(Integer yearRoomId, Short subjectId) {
        // Verifica di sicurezza contro i duplicati
        if (assignmentRepository.findByYearRoomIdAndSubjectId(yearRoomId, subjectId).isPresent()) {
            throw new RuntimeException("Questa materia è già configurata per questa stanza.");
        }

        YearRoom yr = yearRoomRepository.findById(yearRoomId)
                .orElseThrow(() -> new RuntimeException("YearRoom non trovata"));
                
        Subject sub = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Materia non trovata"));

        TeacherAssignment ta = new TeacherAssignment();
        ta.setYearRoom(yr);
        ta.setSubject(sub);
        ta.setEmployee(null); // Inizialmente vacante
        ta.setClassTeacher(false);
        ta.setActive(true); // La nuova materia viene aggiunta come attiva

        assignmentRepository.save(ta);
    }



    /**
     * Rimuove un'assegnazione docente-materia (Staffing)
     * Logica: Se ci sono voti associati, facciamo Soft Delete (isActive = false), altrimenti Hard Delete.
     */
    @Transactional
    public boolean removeAssignment(Integer assignmentId) {
        TeacherAssignment ta = assignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new RuntimeException("Assegnazione non trovata"));

        // --- AGGIUNTA SICUREZZA ---
        // Non permettiamo di rimuovere il Class Teacher da qui (ha un suo flusso dedicato)
        if (ta.isClassTeacher()) {
            throw new RuntimeException("Non puoi rimuovere il Class Teacher tramite questa funzione.");
        }
        
        // Recuperiamo i dati necessari dalle relazioni già mappate
        Short subjectId = ta.getSubject().getId();
        Short yearId = ta.getYearRoom().getYear().getId();
        Integer yearRoomId = ta.getYearRoom().getId();

        // Eseguiamo il controllo incrociato
        boolean hasData = assignmentRepository.hasPhysicalMarks(subjectId, yearId) || 
                        assignmentRepository.hasConductMarks(yearRoomId, yearId);

        if (hasData) {
            // Se ci sono voti, facciamo Soft Delete
            ta.setActive(false);
            assignmentRepository.save(ta);
            return true; // true = Soft Delete eseguito
        } else {
            // Se è un errore di inserimento senza dati, facciamo Hard Delete
            assignmentRepository.delete(ta);
            return false; // false = Hard Delete eseguito
        }
    }


    /**
     * Cambia lo stato di attivazione di un'assegnazione (Staffing).
     * @param assignmentId l'ID della riga in cfg_yearroom_subject_teacher
     * @param active il nuovo stato desiderato
     */
    @Transactional
    public void toggleAssignmentStatus(Integer assignmentId, boolean active) {
        TeacherAssignment ta = assignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new RuntimeException("Assegnazione non trovata"));

        ta.setActive(active);
        // Non serve chiamare save() esplicitamente se siamo in @Transactional, 
        // ma lo mettiamo per chiarezza.
        assignmentRepository.save(ta);
    }





    // GESTIONE SMART COPY DA ANNO PRECEDENTE

    @Transactional
    public void smartCopyFromPreviousYear(Integer targetYearRoomId, SmartCopyRequest request) {
        YearRoom targetRoom = yearRoomRepository.findById(targetYearRoomId)
                .orElseThrow(() -> new RuntimeException("Stanza di destinazione non trovata"));

        YearRoom sourceRoom = yearRoomRepository.findByYearIdAndRoomId(request.previousYearId(), targetRoom.getRoom().getId())
                .orElseThrow(() -> new RuntimeException("Nessuna stanza corrispondente trovata nell'anno sorgente"));

        List<TeacherAssignment> sourceAssignments = assignmentRepository.findByYearRoomId(sourceRoom.getId());
        List<Short> alreadyAssignedSubjectIds = assignmentRepository.findAssignedSubjectIds(targetYearRoomId);

        for (TeacherAssignment sourceAt : sourceAssignments) {
            
            // --- CASO 1: CLASS TEACHER ---
            if (sourceAt.isClassTeacher()) {
                if (request.copyClassTeacher()) {
                    // Verifichiamo se la stanza target ha già un coordinatore
                    boolean alreadyHasClassTeacher = assignmentRepository
                        .findByYearRoomIdAndSubjectIsNullAndClassTeacherTrue(targetYearRoomId).isPresent();
                    
                    if (!alreadyHasClassTeacher) {
                        copyRecord(sourceAt, targetRoom, true); // true forza il tentativo di copia docente
                    }
                }
                continue; 
            }

            // --- CASO 2: SUBJECTS (Staffing) ---
            if (sourceAt.getSubject() != null) {
                Short subjectId = sourceAt.getSubject().getId();
                
                // Salto se la materia esiste già nel target (Punto 2 user)
                if (alreadyAssignedSubjectIds.contains(subjectId)) {
                    continue;
                }

                copyRecord(sourceAt, targetRoom, request.copyTeachers());
            }
        }
    }

    /**
     * Helper per la creazione del record. 
     * Verifica che il docente sia attivo prima di procedere all'assegnazione.
     */
    private void copyRecord(TeacherAssignment source, YearRoom targetRoom, boolean shouldCopyTeacher) {
        TeacherAssignment newAt = new TeacherAssignment();
        newAt.setYearRoom(targetRoom);
        newAt.setSubject(source.getSubject());
        newAt.setClassTeacher(source.isClassTeacher());
        newAt.setActive(true);

        // Se l'utente ha chiesto di copiare il docente (per materia o per classe)
        if (shouldCopyTeacher && source.getEmployee() != null) {
            // VERIFICA ATTIVITÀ DOCENTE (Punto 3 e tua ultima nota)
            if (source.getEmployee().isEmployeeIsActive()) {
                newAt.setEmployee(source.getEmployee());
            } else {
                // Se il docente è inattivo, il record viene creato ma "Not Assigned"
                newAt.setEmployee(null);
            }
        } else {
            newAt.setEmployee(null);
        }

        assignmentRepository.save(newAt);
    }

}
