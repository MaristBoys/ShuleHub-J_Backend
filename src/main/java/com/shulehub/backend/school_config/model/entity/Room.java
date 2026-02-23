package com.shulehub.backend.school_config.model.entity; // <--- Pacchetto per la configurazione scolastica

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "ref_room")
@Data // Genera Getter, Setter, toString, equals e hashCode
@NoArgsConstructor  // Genera il costruttore vuoto richiesto da JPA (risolve l'import alert)
@AllArgsConstructor // Genera il costruttore con tutti i campi (risolve l'import alert)
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @Column(name = "room_name")
    private String roomName;
}