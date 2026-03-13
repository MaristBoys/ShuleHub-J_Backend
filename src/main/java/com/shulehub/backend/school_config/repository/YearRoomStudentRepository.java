package com.shulehub.backend.school_config.repository;



import org.springframework.data.jpa.repository.JpaRepository;
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


}

