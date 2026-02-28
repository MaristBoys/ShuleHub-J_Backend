package com.shulehub.backend.school_config.model.entity; // <--- Pacchetto per la configurazione scolastica

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "ref_year")
@Data // Genera Getter, Setter, toString, equals e hashCode
@NoArgsConstructor  // Genera il costruttore vuoto richiesto da JPA (risolve l'import alert)
@AllArgsConstructor // Genera il costruttore con tutti i campi (risolve l'import alert)
public class Year {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @Column(name = "year", nullable = false)
    private Short year;

    @Column(name = "year_description")
    private String yearDescription;

    @Column(name = "year_is_active")
    private boolean yearIsActive;
}
