package com.eldercare.modules.careplan_management.careplan_design.entity.v2;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "care_goals")
@Data
public class CareGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "care_plan_version_id")
    private Long carePlanVersionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "care_plan_version_id", nullable = false, insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CarePlanVersion carePlanVersion;

    @OneToMany(mappedBy = "careGoal", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<CareIntervention> interventions = new ArrayList<>();



    @Column(name = "problem_or_need",nullable = false)
    private String problemOrNeed;

    @Column(nullable = false)
    private String title;

    @Column(name = "desired_outcome",nullable = false)
    private String desiredOutcome;

    @Column(name = "baseline_value",nullable = false)
    private String baselineValue;

    @Column(name = "target_value")
    private String targetValue;

    @Column(name = "target_unit")

    private String targetUnit;

    @Column(name = "measurement_method")
    private String measurementMethod;

    @Column(name = "target_date")
    private Date targetDate;

    @Column(nullable = false)
    private String priority;

    @Column(nullable = false)
    private String status;

    @Column(name = "resident_preference")
    private String residentPreference;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @Column(name = "updated_by")
    private Long updatedBy;

    public static class CareGoalPriority{
        public static String LOW = "LOW";
        public static String MEDIUM = "MEDIUM";
        public static String HIGH = "HIGH";
    }

    public static class CareGoalStatus{
        public static String ACTIVE = "ACTIVE";
        public static String ACHIEVED = "ACHIEVED";
        public static String DISCONTINUED = "DISCONTINUED";
    }

    public void setStatus(CareGoalStatus status) {
        this.status = status.toString();
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setPriority(CareGoalPriority priority) {
        this.priority = priority.toString();
    }
}
