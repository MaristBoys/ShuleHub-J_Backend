package com.shulehub.backend.auth.model.dto;

import com.shulehub.backend.teacher_assignment.model.dto.TeacherAssignmentDTO; // Importante!
import lombok.Data;
import java.util.List;
import java.util.Set;

@Data
public class TeacherContextDTO {
    // Cambia da List<Map<...>> a List<TeacherAssignmentDTO>
    private List<TeacherAssignmentDTO> assignments; 
    private Set<Integer> classTeacherRoomIds;
}