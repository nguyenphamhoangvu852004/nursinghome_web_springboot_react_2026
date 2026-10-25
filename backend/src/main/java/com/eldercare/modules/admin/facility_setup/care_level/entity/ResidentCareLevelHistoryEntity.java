package com.eldercare.modules.admin.facility_setup.care_level.entity;

import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "resident_care_level_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResidentCareLevelHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private ResidentEntity resident;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "care_level_id", nullable = false)
    private CareLevelEntity careLevel;

    @Column(name = "action", nullable = false)
    private String action;
}

