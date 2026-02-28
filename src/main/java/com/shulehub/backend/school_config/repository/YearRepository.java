package com.shulehub.backend.school_config.repository; // <--- Pacchetto corretto per i repository school_config

import com.shulehub.backend.school_config.model.entity.Year;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface YearRepository extends JpaRepository<Year, Short> {
    // utilizzato al login per caricare il contesto docente
    // utilizzato per inserire l'anno corrente nella dashboard
    Optional<Year> findByYearIsActiveTrue(); 
}
