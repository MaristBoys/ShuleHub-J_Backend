package com.shulehub.backend.school_config.repository;

import com.shulehub.backend.school_config.model.view.YearRoomStatsView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface YearRoomStatsViewRepository extends JpaRepository<YearRoomStatsView, Integer> {
    // Recupera tutte le statistiche delle stanze per l'anno specificato
    List<YearRoomStatsView> findByYearId(Short yearId);
}
