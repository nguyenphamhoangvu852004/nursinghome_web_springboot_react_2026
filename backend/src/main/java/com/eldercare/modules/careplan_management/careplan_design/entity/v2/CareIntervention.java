package com.eldercare.modules.careplan_management.careplan_design.entity.v2;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.Date;


@Entity
@Table(name = "care_interventions")
@Data
public class CareIntervention {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "care_goal_id", nullable = false)
    private Long careGoalId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "care_goal_id", nullable = false, insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CareGoal careGoal;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "instructions", nullable = false, length = 2000)
    private String instructions;

    @Column(name = "responsible_discipline", length = 50)
    private String responsibleDiscipline;

    @Column(name = "assigned_role", nullable = false, length = 50)
    private String assignedRole;

    @Column(name = "frequency_code", nullable = false, length = 30)
    private String frequencyCode;

    @Column(name = "schedule_definition", length = 1000)
    private String scheduleDefinition;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "prn_criteria", length = 1000)
    private String prnCriteria;

    @Column(name = "evidence_or_rationale", length = 1000)
    private String evidenceOrRationale;

    @Column(
            name = "requires_task_generation",
            nullable = false
    )
    private boolean isTaskGenerationRequired = true;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @Column(name = "updated_by")
    private Long updatedBy;

}
