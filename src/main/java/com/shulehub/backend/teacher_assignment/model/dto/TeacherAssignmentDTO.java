package com.shulehub.backend.teacher_assignment.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherAssignmentDTO {
    private Integer yearRoomId;
    private String roomName;
    private Short subjectId;
    private String subjectName;
    private boolean isClassTeacher;
}
