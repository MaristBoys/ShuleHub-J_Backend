package com.shulehub.backend.school_config.repository;

import com.shulehub.backend.school_config.model.entity.YearRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface YearRoomRepository extends JpaRepository<YearRoom, Integer> {

    /**
     * Conta quante stanze sono attive per un determinato ID anno.
     * Fondamentale per la Card della Dashboard.
     */
    long countByYearId(Short yearId);

    /**
     * Recupera tutte le YearRoom di un anno specifico.
     * Nota: grazie ai fetch LAZY, non caricherà subito tutti i dati delle scale
     * a meno che non servano, ottimizzando la memoria.
     */
    List<YearRoom> findByYearId(Short yearId);
}