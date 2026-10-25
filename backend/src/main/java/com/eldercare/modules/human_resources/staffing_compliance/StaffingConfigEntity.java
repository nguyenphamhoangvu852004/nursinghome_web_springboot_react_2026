package com.eldercare.modules.human_resources.staffing_compliance;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "staffing_configs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffingConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "min_hrs_per_resident_day", nullable = false)
    private BigDecimal minHrsPerResidentDay;

    @Column(name = "warn_below_percentage", nullable = false)
    private Integer warnBelowPercentage;

    @Column(name = "facility_id", nullable = false)
    private Long facilityId;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}
