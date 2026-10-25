package com.eldercare.modules.risk_incident.incident_tracking.entity;

import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity; 
import com.eldercare.modules.admin.user_management.UserEntity;                  // TODO: đổi path đúng project (bảng "users")
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "incidents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "incident_type", length = 50, nullable = false)
    private IncidentType incidentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private IncidentStatus status;

    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "sla_deadline", nullable = false)
    private OffsetDateTime slaDeadline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private ResidentEntity resident;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "severity_id", nullable = false)
    private IncidentSeverityEntity severity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by", nullable = false)
    private UserEntity reporter;

    @Column(name = "reported_at", nullable = false)
    private OffsetDateTime reportedAt;

    @Column(name = "witnesses", columnDefinition = "NVARCHAR(MAX)")
    private String witnesses;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    // --- Bổ sung ở mục 0 ---
    @Column(name = "incident_location")
    private String location;

    @Column(name = "occurred_at")
    private OffsetDateTime occurredAt;

    @PrePersist
    void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        if (createdAt == null) createdAt = now;
        if (reportedAt == null) reportedAt = now;
        if (status == null) status = IncidentStatus.OPEN;
    }
}