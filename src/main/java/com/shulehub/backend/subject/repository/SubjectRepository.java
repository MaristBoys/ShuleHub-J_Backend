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


    // Query personalizzata per trovare le materie attive non ancora assegnate a una YearRoom specifica
    @Query("SELECT s FROM Subject s WHERE s.subjectIsActive = true " +
           "AND s.subjectId NOT IN (SELECT ta.subject.subjectId FROM TeacherAssignment ta WHERE ta.yearRoom.yearRoomId = :yearRoomId) " +
           "ORDER BY s.subjectNameEng Asc")
    List<Subject> findAvailableForRoom(@Param("yearRoomId") Integer yearRoomId);
}
