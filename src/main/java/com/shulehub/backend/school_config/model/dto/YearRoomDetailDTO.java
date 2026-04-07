package com.shulehub.backend.school_config.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.shulehub.backend.school_config.model.dto.YearRoomDetailDTO.SelectedScales;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YearRoomDetailDTO {
    // 1. DATI IDENTIFICATIVI & HEADER
    private Integer yearRoomId;
    private Short roomId;
    private String roomName;
    private String formName;
    private String yearName;
    private Boolean isActive; // yearroom_is_active
    
    // Badge di sintesi per l'header
    private Integer studentCount;
    private String classTeacherName;
    private String staffingRatio; 

    // 2. TAB SCALES 
    // Se la stanza è nuova, da Ghost Cell
    // il backend lo popolerà con i suggerimenti.
    private SelectedScales currentScales;


    // 3. TAB STAFFING
    private List<StaffAssignmentInfo> staffAssignments;

    // 4. TAB STUDENTS
    private List<StudentListItemDTO> enrolledStudents;

    // --- SOTTOCLASSI PER LE LISTE ---

    @Data
    @Builder
    public static class SelectedScales {
        private Short gradeScaleId;
        private String gradeScaleName;
        private Short divisionScaleId;
        private String divisionScaleName;
        private Short conductAlphaScaleId;
        private String conductAlphaScaleName;
        private Short conductTextScaleId;
        private String conductTextScaleName;
    }

    @Data
    @Builder
    @NoArgsConstructor  // Aggiungi questo
    @AllArgsConstructor // Aggiungi questo
    public static class StaffAssignmentInfo {
        private Short subjectId;
        private String subjectName;
        private String subjectAbbr;
        private UUID teacherId;
        private String fullName;      // Da Person.fullName
        private boolean isClassTeacher;
        private boolean isTeacherActive;     // Da Employee.employeeIsActive per lo stato del docente
        private boolean isAssignmentActive; // per lo stato del record di assegnazione (cfg_yearroom_subject_teacher.is_active)
        private boolean hasStoredMarks; // Indica se ci sono voti accademici associati a questa materia in questa stanza, utile per la logica di disattivazione o cancellazione.
    
    }

    @Data
    @Builder
    public static class StudentListItemDTO {
        private UUID studentId;
        private String fullName;      // Da Person.fullName
        private boolean isActive;     // Da Student.studentIsActive
    }
}