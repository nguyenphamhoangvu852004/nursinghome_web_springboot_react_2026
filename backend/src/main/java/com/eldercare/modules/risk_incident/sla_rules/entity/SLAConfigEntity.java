package com.eldercare.modules.risk_incident.sla_rules.entity;

import com.eldercare.modules.risk_incident.incident_tracking.entity.IncidentSeverityEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sla_configs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SLAConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sla_window_hrs", nullable = false)
    private Integer slaWindowHrs;

    @Column(name = "external_report_required")
    private Boolean externalReportRequired;

    @Column(name = "regulatory_body")
    private String regulatoryBody;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "severity_id", nullable = false)
    private IncidentSeverityEntity severity;
}
