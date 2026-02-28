package com.shulehub.backend.subject.repository;

import com.shulehub.backend.subject.model.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Short> {

    // Conteggio per la card dashboard
    long countBySubjectIsActiveTrue();

    // Lista filtrata e ordinata alfabeticamente per la gestione attiva (esclude le disattivate)
    List<Subject> findBySubjectIsActiveTrueOrderBySubjectNameEngAsc();
    
    // Lista completa ordinata per visione amministrativa (anche le disattivate)
    List<Subject> findAllByOrderBySubjectNameEngAsc();
}
