package com.shulehub.backend.school_config.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shulehub.backend.school_config.model.entity.YearRoomStudent;

//import java.util.UUID;

@Repository
public interface YearRoomStudentRepository extends JpaRepository<YearRoomStudent, Integer> {
    // Qui potremo aggiungere metodi per contare o spostare studenti se necessario
}

