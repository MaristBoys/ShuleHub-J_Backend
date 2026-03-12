package com.shulehub.backend.school_structure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shulehub.backend.school_structure.model.entity.Form;

import java.util.List;

@Repository
public interface FormRepository extends JpaRepository<Form, Short> {

    /**
     * Recupera tutti i Form attivi ordinati per numero (1, 2, 3...).
     * Indispensabile per costruire le righe della matrice Active Rooms.
     */
    List<Form> findByFormIsActiveTrueOrderByFormNumAsc();


    // Metodo per recuperare tutti i Form ordinati per numero (1, 2, 3...) indipendentemente dallo stato di attivazione
    List<Form> findAllByOrderByFormNumAsc();
}
