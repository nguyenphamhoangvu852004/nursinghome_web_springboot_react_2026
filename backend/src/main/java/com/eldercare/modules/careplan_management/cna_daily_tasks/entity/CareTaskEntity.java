package com.eldercare.modules.careplan_management.cna_daily_tasks.entity;

import com.eldercare.modules.admin.user_management.UserEntity;
import com.eldercare.modules.careplan_management.careplan_design.entity.v2.CareIntervention;
import com.eldercare.modules.careplan_management.cna_daily_tasks.enums.TaskStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "care_tasks")
public class CareTaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 50)
    @NotNull
    @Column(name = "task_type", nullable = false, length = 50)
    private String taskType;

    @NotNull
    @ColumnDefault("'PENDING'")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TaskStatus status;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "is_abnormal_flagged", nullable = false)
    private Boolean isAbnormalFlagged;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "care_intervention_id", nullable = false)
    private CareIntervention careIntervention;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_cna_id")
    private UserEntity assignedCna;

    @NotNull
    @Column(name = "scheduled_time", nullable = false)
    private OffsetDateTime scheduledTime;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @Nationalized
    @Lob
    @Column(name = "goal")
    private String goal;


}
