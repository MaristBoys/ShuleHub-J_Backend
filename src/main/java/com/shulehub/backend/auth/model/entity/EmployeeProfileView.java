package com.shulehub.backend.auth.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Immutable;
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

    @Column(name = "user_id")
    private UUID userId;

    private String username;
    private String email;

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
}
