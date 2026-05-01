package com.shulehub.backend.student_assignment.model.view;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Immutable;
import java.util.UUID;

@Entity
@Immutable
@Table(name = "v_student_picker", schema = "public")
@Data
public class StudentPickerView {

    @Id
    @Column(name = "id_student")
    private UUID idStudent;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "gender", columnDefinition = "char(1)")
    private String gender;

    @Column(name = "prem_number")
    private String premNumber;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "is_dropped")
    private boolean isDropped;

    @Column(name = "last_room_name")
    private String lastRoomName;

    @Column(name = "last_year_val")
    private Short lastYearVal;

    @Column(name = "last_assignment_display")
    private String lastAssignmentDisplay;

    @Column(name = "last_year_id")
    private Short lastYearId;
}