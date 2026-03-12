package com.shulehub.backend.school_config.model.view;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "v_yearroom_details")
@Immutable // Indica a JPA che la vista è in sola lettura
@Data
public class YearRoomDetailView {

    @Id
    @Column(name = "year_room_id")
    private Integer yearRoomId;

    @Column(name = "id_year")
    private Short yearId;

    @Column(name = "id_room")
    private Short roomId;

    @Column(name = "room_name")
    private String roomName;

    @Column(name = "form_id")
    private Short formId;

    @Column(name = "form_num")
    private Short formNum;

    // Mapping Scale
    @Column(name = "id_grade_scale")
    private Short gradeScaleId;
    @Column(name = "grade_scale_name")
    private String gradeScaleName;

    @Column(name = "id_division_scale")
    private Short divisionScaleId;
    @Column(name = "division_scale_name")
    private String divisionScaleName;

    @Column(name = "id_conduct_alpha_scale")
    private Short conductAlphaScaleId;
    @Column(name = "conduct_alpha_name")
    private String conductAlphaScaleName;

    @Column(name = "id_conduct_text_scale")
    private Short conductTextScaleId;
    @Column(name = "conduct_text_name")
    private String conductTextScaleName;
}