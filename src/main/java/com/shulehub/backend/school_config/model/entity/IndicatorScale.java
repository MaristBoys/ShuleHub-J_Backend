package com.shulehub.backend.school_config.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "ref_indicator_scale", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndicatorScale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @Column(name = "scale_name", nullable = false, length = 100)
    private String scaleName;

    @Column(name = "scale_des", columnDefinition = "text")
    private String scaleDescription;

    @Column(name = "indicator_type", nullable = false, length = 10)
    private String indicatorType; // GRADE, DIVISION, CONDUCT

    @Column(name = "scale_is_active", nullable = false)
    private boolean scaleIsActive = true;

    @Column(name = "suggested_for_form_from")
    private Short suggestedForFormFrom;

    @Column(name = "suggested_for_form_to")
    private Short suggestedForFormTo;
}
