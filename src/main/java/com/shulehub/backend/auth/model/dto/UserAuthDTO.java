package com.shulehub.backend.auth.model.dto;

import lombok.Data;
import java.util.Set;
import java.util.UUID;

@Data
public class UserAuthDTO {
    private UUID userId;
    private String username;
    private String email;
    private String profileName; // Preso dal descrittivo della vista
    private Set<String> permissions; // Popolato dal Service
    private TeacherContextDTO teacherData; // Popolato solo se Teacher
}