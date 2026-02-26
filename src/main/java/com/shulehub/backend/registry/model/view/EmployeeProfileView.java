package com.shulehub.backend.registry.model.view;

import com.shulehub.backend.registry.model.dto.ContactItem;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;
import java.time.LocalDate;

@Entity
@Table(name = "v_employee_profile", schema = "public")
@Immutable
@Data
public class EmployeeProfileView {

    @Id
    @Column(name = "employee_id")
    private UUID employeeId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "homeplace_village")
    private String homeplaceVillage;

    @Column(name = "is_visible")
    private boolean isVisible;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "employment_end_date")
    private LocalDate employmentEndDate;

    @Column(name = "employee_is_active")
    private boolean employeeIsActive;

    @Column(name = "employee_note")
    private String employeeNote;

    @Column(name = "gender_label")
    private String genderLabel;

    // --- DATI USER / AUTH ---
    @Column(name = "user_id")
    private UUID userId;

    private String username;

    @Column(name = "login_email")
    private String loginEmail;

    @Column(name = "user_is_active")
    private boolean userIsActive;

    @Column(name = "google_name")
    private String googleName;

    @Column(name = "profile_id")
    private Short profileId;

    @Column(name = "profile_name")
    private String profileName;

    @Column(name = "profile_des")
    private String profileDescription;

    // --- DATI GEOGRAFICI ---
    @Column(name = "id_district")
    private Short idDistrict;

    @Column(name = "district_name")
    private String districtName;

    @Column(name = "id_region")
    private Short idRegion;

    @Column(name = "region_name")
    private String regionName;

    @Column(name = "id_region_group")
    private Short idRegionGroup;

    @Column(name = "region_group_name")
    private String regionGroupName;

    // --- CONTATTI EXTRA ---
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "extra_contacts")
    private List<ContactItem> extraContacts;
}