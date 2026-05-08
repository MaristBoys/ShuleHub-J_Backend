package com.shulehub.backend.school_config.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shulehub.backend.school_config.model.entity.YearRoomStudent;

import java.util.List;

@Repository
public interface YearRoomStudentRepository extends JpaRepository<YearRoomStudent, Integer> {
    
    /**
     * Recupera la lista degli studenti iscritti a una specifica YearRoom.
     * Spring JPA navigherà la relazione 'yearRoom' nell'entità YearRoomStudent 
     * e filtrerà per il suo ID.
     */
    List<YearRoomStudent> findByYearRoomId(Integer yearRoomId);

    /**
     * Recupera la lista degli studenti iscritti a una specifica YearRoom, 
     * includendo i dettagli dello studente e del genere.
     * Utilizza JOIN FETCH per evitare problemi di LazyInitializationException.
     */
    @Query("SELECT yrs FROM YearRoomStudent yrs " +
       "JOIN FETCH yrs.student s " +
       "JOIN FETCH s.person p " +
       "JOIN FETCH p.gender g " +
       "WHERE yrs.yearRoom.id = :yearRoomId")
List<YearRoomStudent> findByYearRoomIdWithDetails(@Param("yearRoomId") Integer yearRoomId);





}


