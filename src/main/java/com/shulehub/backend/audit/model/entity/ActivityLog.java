

package com.shulehub.backend.audit.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;


@Entity
@Table(name = "sys_activity_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "id_user")
    private UUID userId;

    // Aggiunta l'annotazione esplicita del nome colonna
    @Column(name = "identifier", nullable = false)
    private String identifier;

    @Column(name = "action_type", nullable = false)
    private String actionType;

    private String description;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "extra_data", columnDefinition = "jsonb")
    private Map<String, Object> extra_data;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = OffsetDateTime.now();
        }
    }
}