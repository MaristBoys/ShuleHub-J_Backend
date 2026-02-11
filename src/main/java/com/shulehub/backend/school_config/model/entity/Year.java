package com.shulehub.backend.school_config.model.entity; // <--- Pacchetto per la configurazione scolastica

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "ref_year")
@Data
public class Year {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @Column(name = "year_description")
    private String yearDescription;

    @Column(name = "year_is_active")
    private boolean yearIsActive;
}
