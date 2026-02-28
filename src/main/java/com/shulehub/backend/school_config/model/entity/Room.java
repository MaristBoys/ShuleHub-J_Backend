package com.shulehub.backend.school_config.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "ref_room", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @Column(name = "room_name", nullable = false, length = 255, unique = true)
    private String roomName;

    @Column(name = "room_num", nullable = false, unique = true)
    private Short roomNum;

    @Column(name = "room_is_active", nullable = false)
    private boolean roomIsActive = true;

    // Relazione con il Form (es. Form 1, Form 2, ecc.)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_form", nullable = false)
    private Form form;
}