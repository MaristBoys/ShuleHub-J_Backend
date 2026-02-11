package com.shulehub.backend.teacher_assignment.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor // Genera il costruttore necessario per la query JPQL
public class TeacherAssignmentDTO {

    private Integer yearRoomId;
    private String roomName;
    private Short subjectId;
    private String subjectNameKsw;
    private String subjectNameEng;
    private String subjectAbbr;
    private String subjectDescription;
    private boolean isClassTeacher;

    // Nota: Il costruttore generato da @AllArgsConstructor deve seguire 
    // esattamente l'ordine dei campi definiti qui sopra.
}
