package com.shulehub.backend.teacher_assignment.model.dto;

import java.util.UUID;


// DTO per la lista dei teacher che possono insegnare una determinata subject
public record SubjectTeacherSelectionDTO(UUID employeeId, String fullName) {}