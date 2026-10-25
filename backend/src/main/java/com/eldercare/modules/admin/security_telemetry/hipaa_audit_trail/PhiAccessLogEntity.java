package com.eldercare.modules.admin.security_telemetry.hipaa_audit_trail;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.OffsetDateTime;

@Entity
@Table(name = "phi_access_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhiAccessLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_name", nullable = false, length = 100)
    @JsonProperty("table_name")
    private String tableName;

    @Column(name = "record_id", nullable = false, length = 100)
    @JsonProperty("record_id")
    private String recordId;

    @Column(name = "accessed_by")
    @JsonProperty("accessed_by")
    private Long accessedBy;

    @Column(name = "access_type", nullable = false, length = 20)
    @JsonProperty("access_type")
    private String accessType;

    @Column(name = "access_reason", nullable = false, length = 500)
    @JsonProperty("access_reason")
    private String accessReason;

    @Column(name = "ip_address", nullable = false, length = 45)
    @JsonProperty("ip_address")
    private String ipAddress;

    @Column(name = "accessed_at", nullable = false)
    @JsonProperty("accessed_at")
    private OffsetDateTime accessedAt;
}
