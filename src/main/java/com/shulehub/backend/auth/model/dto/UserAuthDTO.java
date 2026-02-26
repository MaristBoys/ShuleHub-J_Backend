package com.shulehub.backend.auth.model.dto;

import lombok.Data;
import java.util.Set;
import java.util.UUID;

@Data
public class UserAuthDTO { //popolato dal service
    private UUID userId;
    private String username;
    private String email;
    private Short profileId;
    private String profileName;
    private String googleName;
    private String pictureUrl; //googlePictureUrl
    private Set<String> permissions; 
    private TeacherContextDTO teacherContext; 
}