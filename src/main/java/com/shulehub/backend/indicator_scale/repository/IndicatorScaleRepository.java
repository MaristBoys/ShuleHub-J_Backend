package com.shulehub.backend.indicator_scale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shulehub.backend.indicator_scale.model.entity.IndicatorScale;

import java.util.List;

@Repository
public interface IndicatorScaleRepository extends JpaRepository<IndicatorScale, Short> {
    
    // Recupera tutte le scale attive per tipo (GRADE, DIVISION, CONDUCT)
    List<IndicatorScale> findByIndicatorTypeAndScaleIsActiveTrue(String type);

    // Query per i suggerimenti basati sul Form 
    // (la query verifica che il numero della Form sia compreso tra suggestedForFormFrom e suggestedForFormTo)
    // fisato il tipo di indicatore (GRADE, DIVISION, CONDUCT)
    @Query("SELECT s FROM IndicatorScale s WHERE s.scaleIsActive = true " +
           "AND s.indicatorType = :type " +
           "AND (:formNum BETWEEN s.suggestedForFormFrom AND s.suggestedForFormTo)")
    List<IndicatorScale> findSuggestedScales(@Param("type") String type, @Param("formNum") Short formNum);

    // Recupera tutte le scale attive ordinate per nome (usato ad esempio per popolare i dropdown di selezione)
    List<IndicatorScale> findByScaleIsActiveTrueOrderByScaleNameAsc();
}