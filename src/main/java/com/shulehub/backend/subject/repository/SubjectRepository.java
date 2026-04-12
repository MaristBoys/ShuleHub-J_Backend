package com.shulehub.backend.subject.repository;

import com.shulehub.backend.subject.model.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * Recupera le materie attive non ancora assegnate a una YearRoom.
     * La clausola NOT EXISTS è sicura anche in presenza di righe con Subject NULL (es. Class Teacher).
     */
    @Query("SELECT s FROM Subject s WHERE s.subjectIsActive = true " +
           "AND NOT EXISTS (" +
           "    SELECT ta FROM TeacherAssignment ta " +
           "    WHERE ta.yearRoom.id = :yearRoomId " +
           "    AND ta.subject.id = s.id" +
           ") " +
           "ORDER BY s.subjectNameEng ASC")
    List<Subject> findAvailableForRoom(@Param("yearRoomId") Integer yearRoomId);

}
