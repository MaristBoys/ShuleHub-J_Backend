package com.shulehub.backend.indicator_scale.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "cfg_indicator_scale_range", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndicatorScaleRange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_indicator_scale", nullable = false)
    private IndicatorScale indicatorScale;

    @Column(name = "indicator_text_value", nullable = false, length = 50)
    private String indicatorTextValue;

    @Column(name = "points")
    private Short points;

    @Column(name = "attribute", length = 100)
    private String attribute;

    @Column(name = "min_value", nullable = false)
    private Short minValue;

    @Column(name = "max_value", nullable = false)
    private Short maxValue;
}
