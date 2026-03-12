package com.shulehub.backend.school_structure.repository; // <--- Pacchetto corretto per i repository school_config

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shulehub.backend.school_structure.model.entity.Year;

import java.util.List;
import java.util.Optional;

@Repository
public interface YearRepository extends JpaRepository<Year, Short> {
    // utilizzato al login per caricare il contesto docente
    // utilizzato per inserire l'anno corrente nella dashboard
    Optional<Year> findByYearIsActiveTrue(); 
    
    // Aggiunta per ordinare gli anni in modo decrescente (utile per la UI)
    List<Year> findAllByOrderByYearDesc();

    // Trova l'anno con il valore numerico più alto
    Optional<Year> findFirstByOrderByYearDesc();

    // Disattiva l'anno attivo corrente (utilizzato durante il rollover)
    @Modifying
    @Query("UPDATE Year y SET y.yearIsActive = false WHERE y.yearIsActive = true")
    void deactivateActiveYear();
}
