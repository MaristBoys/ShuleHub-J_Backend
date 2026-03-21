package com.shulehub.backend.school_config.model.view;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Immutable;
import java.util.UUID;

@Entity
@Immutable
@Table(name = "v_yearroom_stats", schema = "public")
@Data
public class YearRoomStatsView {

    @Id
    @Column(name = "id_yearroom")
    private Integer yearRoomId;

    @Column(name = "id_year")
    private Short yearId;

    @Column(name= "yearroom_is_active")
    private boolean yearroomIsActive;

    @Column(name = "student_count")
    private Integer studentCount;

    @Column(name = "id_class_teacher")
    private UUID classTeacherId;

    @Column(name = "class_teacher_name")
    private String classTeacherName;

    @Column(name = "assigned_subjects")
    private Integer assignedSubjects;

    @Column(name = "total_subjects")
    private Integer totalSubjects;
}
