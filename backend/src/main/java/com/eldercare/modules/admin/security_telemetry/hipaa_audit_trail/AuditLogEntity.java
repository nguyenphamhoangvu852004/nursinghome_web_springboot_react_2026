package com.eldercare.modules.admin.security_telemetry.hipaa_audit_trail;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.OffsetDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_name", nullable = false, length = 100)
    @JsonProperty("table_name")
    private String tableName;

    @Column(name = "record_id", nullable = false, length = 100)
    @JsonProperty("record_id")
    private String recordId;

    @Column(nullable = false, length = 20)
    private String action;

    @Column(name = "old_data", columnDefinition = "NVARCHAR(MAX)")
    @JsonProperty("old_data")
    private String oldData;

    @Column(name = "new_data", columnDefinition = "NVARCHAR(MAX)")
    @JsonProperty("new_data")
    private String newData;

    @Column(name = "performed_by")
    @JsonProperty("performed_by")
    private Long performedBy;

    @Column(name = "performed_at", nullable = false)
    @JsonProperty("performed_at")
    private OffsetDateTime performedAt;

    @Column(name = "ip_address", nullable = false, length = 45)
    @JsonProperty("ip_address")
    private String ipAddress;
}
