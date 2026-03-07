package com.shulehub.backend.school_config.repository;

import com.shulehub.backend.school_config.model.entity.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FormRepository extends JpaRepository<Form, Short> {

    /**
     * Recupera tutti i Form attivi ordinati per numero (1, 2, 3...).
     * Indispensabile per costruire le righe della matrice Active Rooms.
     */
    List<Form> findByFormIsActiveTrueOrderByFormNumAsc();
}
