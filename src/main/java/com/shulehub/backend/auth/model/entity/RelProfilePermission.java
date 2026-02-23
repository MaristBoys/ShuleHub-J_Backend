package com.shulehub.backend.auth.model.entity; 

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "rel_profile_permission")
@Data // Genera Getter, Setter, toString, equals e hashCode
@NoArgsConstructor  // Genera il costruttore vuoto richiesto da JPA (risolve l'import alert)
@AllArgsConstructor // Genera il costruttore con tutti i campi (risolve l'import alert)
public class RelProfilePermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_profile")
    private Short idProfile;

    // Invece di un semplice Short idPermission, usiamo l'oggetto!
    @ManyToOne
    @JoinColumn(name = "id_permission", referencedColumnName = "id")
    private RefPermission permission;
}
