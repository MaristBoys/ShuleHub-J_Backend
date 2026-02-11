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

    @Column(name = "id_year")
    private Short idYear;

    @Column(name = "id_room")
    private Short idRoom;
}
