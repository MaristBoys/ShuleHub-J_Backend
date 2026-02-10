package com.shulehub.backend.auth.mapper;

import com.shulehub.backend.auth.model.entity.EmployeeProfileView;
import com.shulehub.backend.auth.model.dto.UserAuthDTO;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public UserAuthDTO toAuthDTO(EmployeeProfileView view) {
        if (view == null) return null;

        UserAuthDTO dto = new UserAuthDTO();
        dto.setUserId(view.getUserId());
        dto.setUsername(view.getUsername());
        dto.setEmail(view.getEmail());
        dto.setProfileName(view.getProfileName());
        // permissions e teacherData verranno settati nel Service
        return dto;
    }
}