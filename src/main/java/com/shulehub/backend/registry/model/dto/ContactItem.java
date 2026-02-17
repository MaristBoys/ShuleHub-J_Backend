package com.shulehub.backend.registry.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Questa classe serve per mappare gli oggetti contenuti nell'array JSONB 
 * (extra_contacts) restituito dalle viste del database (es. v_employee_profile).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactItem {

    @JsonProperty("id_contact")
    private UUID idContact;

    @JsonProperty("contact_type_name")
    private String contactTypeName;

    private String value;

    @JsonProperty("is_primary")
    private boolean isPrimary;

    private String note;
}
