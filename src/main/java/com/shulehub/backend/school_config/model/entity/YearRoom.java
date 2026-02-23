package com.shulehub.backend.school_config.model.entity; // <--- Pacchetto coerente con Year e Room

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// Import delle entità correlate
//import com.shulehub.backend.school_config.model.entity.Year; //stesso package, non serve import
//import com.shulehub.backend.school_config.model.entity.Room;


@Entity
@Table(name = "cfg_year_room")
@Data // Genera Getter, Setter, toString, equals e hashCode
@NoArgsConstructor  // Genera il costruttore vuoto richiesto da JPA (risolve l'import alert)
@AllArgsConstructor // Genera il costruttore con tutti i campi (risolve l'import alert)
public class YearRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Invece di Short idYear, usiamo l'oggetto Year
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_year", nullable = false)
    private Year year; 

    // Invece di Short idRoom, usiamo l'oggetto Room
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_room", nullable = false)
    private Room room;
    
}
