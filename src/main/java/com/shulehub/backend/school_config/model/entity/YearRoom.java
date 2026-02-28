package com.shulehub.backend.school_config.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "cfg_year_room", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class YearRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Relazione con l'Anno scolastico
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_year", nullable = false)
    private Year year;

    // Relazione con la Stanza (Room)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_room", nullable = false)
    private Room room;

    // --- SCALE DI VALUTAZIONE (IndicatorScale) ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_grade_scale", nullable = false)
    private IndicatorScale gradeScale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_division_scale", nullable = false)
    private IndicatorScale divisionScale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_conduct_alpha_scale")
    private IndicatorScale conductAlphaScale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_conduct_text_scale")
    private IndicatorScale conductTextScale;
}