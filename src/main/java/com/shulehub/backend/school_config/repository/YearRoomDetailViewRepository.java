package com.shulehub.backend.school_config.repository;

import com.shulehub.backend.school_config.model.view.YearRoomDetailView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface YearRoomDetailViewRepository extends JpaRepository<YearRoomDetailView, Integer> {
    // Recupera i dettagli piatti della stanza tramite l'ID della YearRoom dalla vista creata appositamente
    Optional<YearRoomDetailView> findByYearRoomId(Integer yearRoomId);
}