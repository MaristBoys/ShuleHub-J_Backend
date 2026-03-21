package com.shulehub.backend.teacher_assignment.model.dto;

import java.util.UUID;


// DTO per la ista di tutti gli employee attivi che possono diventare class teacher di una yearroom
public record ClassTeacherSelectionDTO(UUID employeeId, String fullName) {}