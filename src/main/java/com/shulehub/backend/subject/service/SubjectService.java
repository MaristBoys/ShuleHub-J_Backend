package com.shulehub.backend.subject.service;

import com.shulehub.backend.subject.model.entity.Subject;
import com.shulehub.backend.subject.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;

    /**
     * Recupera tutte le materie per la gestione amministrativa
     */
    @Transactional(readOnly = true)
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAllByOrderBySubjectNameEngAsc();
    }

     /**
     * Recupera tutte le materie attive (per dropdown, assegnazioni, ecc.)
     */ 
    @Transactional(readOnly = true)
    public List<Subject> getActiveSubjects() {
        return subjectRepository.findBySubjectIsActiveTrueOrderBySubjectNameEngAsc();
     }

     // Conta quante materie attive ci sono (utile per mostrare il numero totale nella UI)
     @Transactional(readOnly = true)
    public long countActiveSubjects() {
        return subjectRepository.countBySubjectIsActiveTrue();
    }


     /**
     * Recupera una singola materia per ID (Utility per l'orchestratore)
     */
    @Transactional(readOnly = true)
    public Subject getSubjectById(Short id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + id));
    }

     /**
     * Crea una nuova materia
     */
    @Transactional
    public Subject createSubject(Subject subject) {
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



    
    

    

    
