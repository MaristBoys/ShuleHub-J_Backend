package com.shulehub.backend.school_config.model.entity; // <--- Pacchetto coerente con Year e Room

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "cfg_year_room")
@Data
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
