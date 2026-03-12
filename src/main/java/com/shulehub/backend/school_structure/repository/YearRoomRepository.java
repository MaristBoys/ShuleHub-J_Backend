package com.shulehub.backend.school_structure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shulehub.backend.school_structure.model.entity.YearRoom;

import java.util.List;
import java.util.Optional;


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

    /**
     * Recupera una stanza specifica per un determinato anno.
     * Utilizzato per la logica di rollover (recupero scale anno precedente).
     */
    Optional<YearRoom> findByYearIdAndRoomId(Short yearId, Short roomId);
}


