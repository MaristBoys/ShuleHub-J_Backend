package com.shulehub.backend.registry.model.dto;

//COSTRUITO SULLA BASE DELLA VISTA EmployeeProfileView

import com.shulehub.backend.registry.model.dto.ContactItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeProfileDTO {

    // Identificativi
    private UUID employeeId;
    private UUID userId;

    // Anagrafica Personale
    private String firstName;
    private String middleName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String genderLabel;
    private String homeplaceVillage;

    // Dati Lavorativi
    private LocalDate hireDate;
    private LocalDate employmentEndDate;
    private boolean employeeIsActive;
    private String employeeNote;

    // Dati Account & Profilo
    private String username;
    private String loginEmail;
    private boolean userIsActive;
    private String googleName;
    private Short profileId;
    private String profileName;
    private String profileDescription;

    // Geografia
    private Integer idDistrict;
    private String districtName;
    private Integer idRegion;
    private String regionName;
    private String regionGroupName;

    // Contatti Extra (JSON trasformato in lista di oggetti)
    private List<ContactItem> extraContacts;
}
