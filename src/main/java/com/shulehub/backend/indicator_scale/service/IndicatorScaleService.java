package com.shulehub.backend.indicator_scale.service;

import com.shulehub.backend.indicator_scale.model.dto.IndicatorScaleDTO;
import com.shulehub.backend.indicator_scale.model.entity.IndicatorScale;
import com.shulehub.backend.indicator_scale.repository.IndicatorScaleRangeRepository;
import com.shulehub.backend.indicator_scale.repository.IndicatorScaleRepository;
import com.shulehub.backend.school_config.model.dto.YearRoomDetailDTO;
import com.shulehub.backend.school_config.model.view.YearRoomDetailView;
import com.shulehub.backend.school_structure.repository.YearRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IndicatorScaleService {

    private final IndicatorScaleRepository indicatorScaleRepository;
    private final IndicatorScaleRangeRepository indicatorScaleRangeRepository;
    private final YearRoomRepository yearRoomRepository;



    // Metodo per recuperare una scala specifica (usato ad esempio per mostrare i dettagli di una scala selezionata)
    @Transactional(readOnly = true)
    public IndicatorScale getScaleById(Short id) {
        if (id == null) return null;
        return indicatorScaleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Indicator Scale not found with id: " + id));
    }

    // Metodo per recuperare tutte le scale attive
    @Transactional(readOnly = true)
    public List<IndicatorScaleDTO> getAllActiveScales() {
        return indicatorScaleRepository.findByScaleIsActiveTrueOrderByScaleNameAsc().stream()
            .map(scale -> {
                IndicatorScaleDTO dto = new IndicatorScaleDTO();
                dto.setId(scale.getId());
                dto.setScaleName(scale.getScaleName());
                dto.setIndicatorType(scale.getIndicatorType());
                
                // Mappatura dei range (riutilizzando la logica esistente nel tuo service)
                dto.setRanges(indicatorScaleRangeRepository.findByIndicatorScaleIdOrderByMinValueDesc(scale.getId()).stream()
                    .map(r -> {
                        IndicatorScaleDTO.ScaleRangeDTO rangeDto = new IndicatorScaleDTO.ScaleRangeDTO();
                        rangeDto.setTextValue(r.getIndicatorTextValue());
                        rangeDto.setMinValue(r.getMinValue());
                        rangeDto.setMaxValue(r.getMaxValue());
                        rangeDto.setAttribute(r.getAttribute());
                        rangeDto.setPoints(r.getPoints());
                        return rangeDto;
                    })
                    .collect(Collectors.toList()));
                return dto;
            }).collect(Collectors.toList());
    }





    // Metodo per recuperare tutte le scale attive di un certo tipo (es. GRADE, DIVISION, CONDUCT_ALPHA)
    @Transactional(readOnly = true)
    public List<IndicatorScaleDTO> getAvailableScales(String type) {
        return indicatorScaleRepository.findByIndicatorTypeAndScaleIsActiveTrue(type).stream()
            .map(scale -> {
                IndicatorScaleDTO dto = new IndicatorScaleDTO();
                dto.setId(scale.getId());
                dto.setScaleName(scale.getScaleName());
                dto.setIndicatorType(scale.getIndicatorType());
                
                dto.setRanges(indicatorScaleRangeRepository.findByIndicatorScaleIdOrderByMinValueDesc(scale.getId()).stream()
                    .map(r -> {
                        IndicatorScaleDTO.ScaleRangeDTO rangeDto = new IndicatorScaleDTO.ScaleRangeDTO();
                        // Assicurati che nel DTO il campo si chiami textValue
                        rangeDto.setTextValue(r.getIndicatorTextValue());
                        rangeDto.setMinValue(r.getMinValue());
                        rangeDto.setMaxValue(r.getMaxValue());
                        rangeDto.setAttribute(r.getAttribute());
                        rangeDto.setPoints(r.getPoints());
                        return rangeDto;
                    })
                    .collect(Collectors.toList()));
                return dto;
            }).collect(Collectors.toList());
    }

    
    /**
     * Recupera i suggerimenti basati esclusivamente sul numero della Form.
     * Restituisce un oggetto SelectedScales già popolato con nomi e ID.
     */
    @Transactional(readOnly = true)
    public YearRoomDetailDTO.SelectedScales getSuggestedScalesByForm(Short formNum) {
        Map<String, Short> ids = new HashMap<>();
        
        // Eseguiamo la ricerca per ogni tipo di indicatore
        findTopSuggested(ids, "GRADE", formNum);
        findTopSuggested(ids, "DIVISION", formNum);
        findTopSuggested(ids, "CONDUCT_ALPHA", formNum);
        findTopSuggested(ids, "CONDUCT_TEXT", formNum);

        // Costruiamo l'oggetto per il DTO recuperando i nomi corrispondenti
        return YearRoomDetailDTO.SelectedScales.builder()
                .gradeScaleId(ids.get("GRADE"))
                .gradeScaleName(getScaleNameOrDefault(ids.get("GRADE")))
                
                .divisionScaleId(ids.get("DIVISION"))
                .divisionScaleName(getScaleNameOrDefault(ids.get("DIVISION")))
                
                .conductAlphaScaleId(ids.get("CONDUCT_ALPHA"))
                .conductAlphaScaleName(getScaleNameOrDefault(ids.get("CONDUCT_ALPHA")))
                
                .conductTextScaleId(ids.get("CONDUCT_TEXT"))
                .conductTextScaleName(getScaleNameOrDefault(ids.get("CONDUCT_TEXT")))
                .build();
    }

    /**
     * Metodo Helper: Cerca la prima scala suggerita nel DB che soddisfa il range del formNum.
     * Utilizza la query personalizzata nel repository.
     */
    private void findTopSuggested(Map<String, Short> map, String type, Short formNum) {
        if (formNum == null) return;
        
        // findSuggestedScales è il metodo nel tuo repository con la clausola BETWEEN
        indicatorScaleRepository.findSuggestedScales(type, formNum).stream()
            .findFirst()
            .ifPresent(scale -> map.put(type, scale.getId()));
    }

    /**
     * Metodo Helper: Recupera il nome della scala per il DTO o un default se non trovata.
     */
    private String getScaleNameOrDefault(Short id) {
        if (id == null) return "Not Assigned";
        return indicatorScaleRepository.findById(id)
                .map(IndicatorScale::getScaleName)
                .orElse("Not Assigned");
    }
/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */

/*    public Map<String, Short> getSuggestedScalesByFormNum(Short formNum) {
        Map<String, Short> suggestions = new HashMap<>();
        
        // Se formNum è null, evitiamo crash e restituiamo una mappa vuota o dei default assoluti
        if (formNum == null) {
            return suggestions;
        }

        // Logica basata sul livello (formNum): 
        // Assumiamo Form 1-4 = O-Level/Primary, Form 5-6 = A-Level/Advanced
        if (formNum <= 4) {
            suggestions.put("GRADE", (short) 1);         // ID scala O-Level
            suggestions.put("DIVISION", (short) 3);      // ID scala O-Level
            suggestions.put("CONDUCT_ALPHA", (short) 5); 
            suggestions.put("CONDUCT_TEXT", (short) 6);
        } else {
            suggestions.put("GRADE", (short) 2);         // ID scala A-Level
            suggestions.put("DIVISION", (short) 4);      // ID scala A-Level
            suggestions.put("CONDUCT_ALPHA", (short) 5);
            suggestions.put("CONDUCT_TEXT", (short) 6);
        }
        
        return suggestions;
    }
*/  
    
    
    /**
     * CALCOLO SUGGERIMENTI - NON USATO
     * Questo metodo DEVE accettare YearRoomDetailView
     */
/*
    @Transactional(readOnly = true)
    public Map<String, Short> calculateSuggestedScales(YearRoomDetailView currentView) {
        Map<String, Short> suggestions = new HashMap<>();
        
        // 1. Identifica l'anno precedente
        short previousYearId = (short) (currentView.getYearId() - 1);
        
        // 2. Cerca continuità dall'anno scorso
        yearRoomRepository.findByYearIdAndRoomId(previousYearId, currentView.getRoomId())
            .ifPresent(prevRoom -> {
                if (prevRoom.getGradeScale() != null) 
                    suggestions.put("GRADE", prevRoom.getGradeScale().getId());
                if (prevRoom.getDivisionScale() != null) 
                    suggestions.put("DIVISION", prevRoom.getDivisionScale().getId());
                if (prevRoom.getConductAlphaScale() != null) 
                    suggestions.put("CONDUCT_ALPHA", prevRoom.getConductAlphaScale().getId());
            });

        // 3. Fallback basato sul Form (es. Primary vs Secondary)
        if (!suggestions.containsKey("GRADE")) {
            findTopSuggested(suggestions, "GRADE", currentView.getFormNum());
        }
        if (!suggestions.containsKey("DIVISION")) {
            findTopSuggested(suggestions, "DIVISION", currentView.getFormNum());
        }
        
        return suggestions;
    }

    private void findTopSuggested(Map<String, Short> map, String type, Short formNum) {
        indicatorScaleRepository.findSuggestedScales(type, formNum).stream()
            .findFirst()
            .ifPresent(scale -> map.put(type, scale.getId()));
    }
    */        
}
     