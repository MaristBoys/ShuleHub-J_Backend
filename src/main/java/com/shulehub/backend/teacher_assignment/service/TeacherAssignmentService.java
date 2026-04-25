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
        // 1. Validazione preventiva: subjectId non può essere null qui
        if (subjectId == null) {
            throw new RuntimeException("subjectId obbligatorio per l'assegnazione dello Staffing");
        }


        // 1. Recupera l'assegnazione esistente
        Optional<TeacherAssignment> existingOpt = assignmentRepository
                .findByYearRoomIdAndSubjectId(yearRoomId, subjectId);

        // 2. Gestione Unassign (Fondamentale!)
        if (employeeId == null) {
            existingOpt.ifPresent(assignment -> {
                assignment.setEmployee(null);
                assignmentRepository.save(assignment);
            });
            return;
        }


        // 3. Gestione Assegnazione/Cambio
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

               
        // Cerchiamo l'assegnazione esistente (anche se isActive = false)
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

        // 4. Aggiorniamo il docente
        assignment.setEmployee(employee);

        // 5. LOGICA CRITICA: Se la materia era disattivata, la riattiviamo
        // Questo permette di "recuperare" una materia archiviata semplicemente riassegnando un docente
        assignment.setActive(true);

        assignmentRepository.save(assignment);
    }
*/
 
    @Transactional
    public void assignSubjectTeacher(Integer yearRoomId, Short subjectId, UUID employeeId) {
        if (subjectId == null) {
            throw new RuntimeException("subjectId obbligatorio per l'assegnazione dello Staffing");
        }

        // 1. Cerchiamo l'assegnazione una sola volta
        TeacherAssignment assignment = assignmentRepository
                .findByYearRoomIdAndSubjectId(yearRoomId, subjectId)
                .orElse(null);

        // 2. CASO RIMOZIONE (Unassign)
        if (employeeId == null) {
            if (assignment != null) {
                assignment.setEmployee(null);
                // Opzionale: assignment.setActive(false); // Se vuoi disattivare la riga quando togli il docente
                assignmentRepository.save(assignment);
            }
            return; 
        }

        // 3. CASO ASSEGNAZIONE / CAMBIO
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

        // Se non esiste l'assegnazione, la creiamo
        if (assignment == null) {
            YearRoom yearRoom = yearRoomRepository.findById(yearRoomId)
                    .orElseThrow(() -> new RuntimeException("YearRoom not found"));
            Subject subject = subjectRepository.findById(subjectId)
                    .orElseThrow(() -> new RuntimeException("Subject not found"));

            assignment = new TeacherAssignment();
            assignment.setYearRoom(yearRoom);
            assignment.setSubject(subject);
            assignment.setClassTeacher(false);
        }

        // Aggiorniamo i dati comuni
        assignment.setEmployee(employee);
        assignment.setActive(true); 

        assignmentRepository.save(assignment);
    }


    /**
     * Recupera tutte le assegnazioni (Staffing) per una stanza, mappate in DTO.
     * Utilizza il nuovo metodo del repository che fa già il LEFT JOIN per avere tutte le info necessarie.
     */
    public List<YearRoomDetailDTO.StaffAssignmentInfo> getStaffAssignmentsForRoom(Integer yearRoomId) {
        List<TeacherAssignment> assignments = assignmentRepository.findStaffingByYearRoomId(yearRoomId);

        return assignments.stream()
            .map(ta -> {
                Short sId = (ta.getSubject() != null) ? ta.getSubject().getId() : null;
                Short yId = ta.getYearRoom().getYear().getId();
                
                // Fallback nomi
                String sName = (ta.getSubject() != null) ? ta.getSubject().getSubjectNameEng() : "Class Coordination";
                String sAbbr = (ta.getSubject() != null) ? ta.getSubject().getSubjectAbbr() : "CC";
                
                // Info Docente
                UUID tId = (ta.getEmployee() != null) ? ta.getEmployee().getId() : null;
                String fName = "Not Assigned";
                boolean active = false;
                if (ta.getEmployee() != null) {
                    active = ta.getEmployee().isEmployeeIsActive();
                    if (ta.getEmployee().getPerson() != null) {
                        fName = ta.getEmployee().getPerson().getFullName();
                    }
                }

                // LOGICA FILTRO VOTI (Sincronizzata con i nuovi parametri Repository)
                boolean hasMarks = false;
                if (sId != null) {
                    // Per le materie: check voti materia OR check condotta (se la materia è legata alla condotta)
                    hasMarks = assignmentRepository.hasPhysicalMarks(sId, yearRoomId, yId) || 
                            assignmentRepository.hasConductMarks(yearRoomId, yId);
                } else if (ta.isClassTeacher()) {
                    // Per il Class Teacher: check solo condotta
                    hasMarks = assignmentRepository.hasConductMarks(yearRoomId, yId);
                }

                return YearRoomDetailDTO.StaffAssignmentInfo.builder()
                    .subjectId(sId)
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
     * Aggiunge in bulk più materie alla stanza (usato per il multi-select).
     */
    @Transactional
    public void bulkAssignSubjects(Integer yearRoomId, List<Short> subjectIds) {
        YearRoom yr = yearRoomRepository.findById(yearRoomId)
                .orElseThrow(() -> new RuntimeException("YearRoom not found"));

        for (Short sId : subjectIds) {
            Subject s = subjectRepository.findById(sId)
                    .orElseThrow(() -> new RuntimeException("Subject not found: " + sId));
            
            // Creiamo l'assegnazione vuota (senza docente)
            TeacherAssignment ta = new TeacherAssignment();
            ta.setYearRoom(yr);
            ta.setSubject(s);
            ta.setEmployee(null); // Inizialmente vacante
            ta.setClassTeacher(false);
            ta.setActive(true); // La nuova materia viene aggiunta come attiva
            
            assignmentRepository.save(ta);
        }
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
        // 1. Non permettiamo di rimuovere il Class Teacher da qui (ha un suo flusso dedicato)
        if (ta.isClassTeacher()) {
            throw new RuntimeException("Non puoi rimuovere il Class Teacher tramite questa funzione.");
        }
        
        // 2. Extract IDs safely (preventing NullPointerException on subject)
        // Se non c'è materia e non è Class Teacher, è un record orfano: Hard Delete senza controlli sui voti
        if (ta.getSubject() == null) {
            assignmentRepository.delete(ta);
            return false;
        }


        // Recuperiamo i dati necessari dalle relazioni già mappate
        Short subjectId = ta.getSubject().getId();
        Short yearId = ta.getYearRoom().getYear().getId();
        Integer yearRoomId = ta.getYearRoom().getId();

        // 3. Eseguiamo il controllo incrociato
        boolean hasData = assignmentRepository.hasPhysicalMarks(subjectId, yearRoomId, yearId) || 
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
    public boolean toggleAssignmentStatus(Integer yearRoomId, Short subjectId) {
        TeacherAssignment assignment = assignmentRepository
                .findByYearRoomIdAndSubjectId(yearRoomId, subjectId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        boolean newState = !assignment.isActive();
        assignment.setActive(newState);
        assignmentRepository.save(assignment);
        return newState;
    }




    // recupera le stanze idonee a essere sorgenti per la funzione di Smart Copy 
    // (stesso Form, anno corrente o precedente, escludendo la stanza target)
    public List<YearRoomDetailDTO> getEligibleSourceRooms(Integer targetYearRoomId) {
        // 1. Recupero la stanza target
        YearRoom target = yearRoomRepository.findById(targetYearRoomId)
                .orElseThrow(() -> new RuntimeException("Target room not found"));
        
        Short currentYearId = target.getYear().getId();
        Short previousYearId = (short) (currentYearId - 1);
        Short formId = target.getRoom().getForm().getId();

        // 2. Cerco le stanze dello stesso Form (Anno Corrente e Precedente)
        // Nota: Ho aggiunto un filtro per assicurarmi di non suggerire stanze non ancora "attivate" 
        // se la logica lo richiede, ma per ora teniamo tutte le YearRoom del form.
        return yearRoomRepository.findAll().stream()
                .filter(yr -> yr.getRoom().getForm().getId().equals(formId))
                .filter(yr -> yr.getYear().getId().equals(currentYearId) || yr.getYear().getId().equals(previousYearId))
                .filter(yr -> !yr.getId().equals(targetYearRoomId))
                .map(yr -> {
                    // Se il costruttore YearRoomDetailDTO() continua a darti noie, 
                    // assicurati che la classe sia importata correttamente.
                    YearRoomDetailDTO dto = new YearRoomDetailDTO();
                    dto.setYearRoomId(yr.getId());
                    
                    // Formattiamo il nome per renderlo chiaro nel dropdown: "1A (2023/2024)"
                    String yearName = yr.getYear().toString(); // Assumendo che Year abbia un toString ben formattato
                    String roomName = yr.getRoom().getRoomName();
                    dto.setRoomName(roomName + " (" + yearName + ")");
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /* effettua la copia massiva della configurazione da una stanza sorgente a una stanza target,
    con la logica guidata dai parametri ricevuti nella SmartCopyRequest
     */
    @Transactional
    public void smartCopyFromRoom(Integer targetYearRoomId, SmartCopyRequest request) {
        YearRoom targetRoom = yearRoomRepository.findById(targetYearRoomId)
                .orElseThrow(() -> new RuntimeException("Target room not found"));

        // 1. Recupero le assegnazioni della stanza SORGENTE
        List<TeacherAssignment> sourceAssignments = assignmentRepository.findByYearRoomId(request.sourceYearRoomId());
        
        // 2. Recupero cosa c'è già nel target per evitare duplicati
        List<Short> alreadyAssignedSubjectIds = assignmentRepository.findByYearRoomId(targetYearRoomId)
                .stream()
                .map(at -> at.getSubject().getId())
                .toList();

        for (TeacherAssignment sourceAt : sourceAssignments) {
            // 1. FILTRO MATERIE ATTIVE (NEW): 
            // Se la materia di origine è disattivata nel catalogo generale, la saltiamo.
            if (sourceAt.getSubject() != null && !sourceAt.getSubject().isSubjectIsActive()) {
                continue; 
            }
            
            // 2. Se è un Class Teacher e l'utente NON ha spuntato "Include Class Teacher", salto
            if (sourceAt.isClassTeacher() && !request.copyClassTeacher()) {
                continue; 
            }
            

            // 3. Evitiamo duplicati (ma vengono prese tutte)
            Short subjectId = sourceAt.getSubject().getId();
            if (subjectId != null && alreadyAssignedSubjectIds.contains(subjectId)) {
                continue;
    }
            // 4. Eseguo la copia usando l'helper 
            copyRecord(sourceAt, targetRoom, request);
        }
    }

    private void copyRecord(TeacherAssignment source, YearRoom targetRoom, SmartCopyRequest request) {
        TeacherAssignment newAt = new TeacherAssignment();
        
        newAt.setYearRoom(targetRoom);
        newAt.setSubject(source.getSubject());
        newAt.setClassTeacher(source.isClassTeacher());
        newAt.setActive(true);

        // Determiniamo se dobbiamo tentare di copiare il docente
        // Se è CT, guardiamo request.copyClassTeacher(). Se è materia, request.copyTeachers().
        boolean shouldAttemptTeacherCopy = source.isClassTeacher() ? 
                                        request.copyClassTeacher() : 
                                        request.copyTeachers();

        if (shouldAttemptTeacherCopy && source.getEmployee() != null) {
            // Verifica se il docente è ancora attivo (usando il tuo metodo isEmployeeIsActive)
            if (Boolean.TRUE.equals(source.getEmployee().isEmployeeIsActive())) {
                newAt.setEmployee(source.getEmployee());
            } else {
                newAt.setEmployee(null); // Docente rimosso/inattivo -> "Not Assigned"
            }
        } else {
            newAt.setEmployee(null);
        }

        assignmentRepository.save(newAt);
    }

}
