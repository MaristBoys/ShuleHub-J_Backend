package com.shulehub.backend.indicator_scale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shulehub.backend.indicator_scale.model.entity.IndicatorScaleRange;

import java.util.List;

@Repository
public interface IndicatorScaleRangeRepository extends JpaRepository<IndicatorScaleRange, Integer> {
    
    // Recupera tutti i range di una specifica scala ordinati per valore minimo (per la legenda)
    List<IndicatorScaleRange> findByIndicatorScaleIdOrderByMinValueDesc(Short scaleId);
}