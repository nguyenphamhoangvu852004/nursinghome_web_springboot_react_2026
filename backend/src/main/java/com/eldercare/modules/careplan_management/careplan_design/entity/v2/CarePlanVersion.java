package com.eldercare.modules.careplan_management.careplan_design.entity.v2;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "care_plan_versions")
@Builder
public class CarePlanVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "care_plan_id", nullable = false)
    private Long carePlanId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "care_plan_id", nullable = false, insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    private CarePlan carePlan = null;

    @OneToMany(mappedBy = "carePlanVersion", fetch = FetchType.LAZY)
    @Builder.Default
    @JsonIgnore
    @ToString.Exclude
    private List<CareGoal> goals = new ArrayList<>();

    @Column(name = "version_number")
    private int versionNumber;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "based_on_version_id")
    private Long basedOnVersionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "based_on_version_id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    private CarePlanVersion basedOnVersion;

    @Column(name = "based_on_admission_id")
    private Long basedOnAdmissionId;

    @Column(name = "created_from_review_id")
    private Long createdFromReviewId;

    @Column(name = "change_summary")
    private String changeSummary;

    @Column(name = "submitted_at")
    private OffsetDateTime submittedAt;

    @Column(name = "submitted_by")
    private Long submmitedBy;

    @Column(name = "approved_at")
    private OffsetDateTime approvedAt;

    @Column(name = "approved_by")
    private Long approvedby;

    @Column(name = "rejected_at")
    private OffsetDateTime rejectedAt;

    @Column(name = "rejected_by")
    private Long rejectedBy;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "effective_from")
    private OffsetDateTime effectiveFrom;

    @Column(name = "effective_to")
    private OffsetDateTime effectiveTo;

    @Column(name = "content_hash")
    private byte[] contentHash;

    @Builder.Default
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "created_by")
    private Long createdBy;


    @Builder.Default
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @Column(name = "updated_by")
    private Long updatedBy;

    @Version
    @Generated(event = { EventType.INSERT, EventType.UPDATE } )
    @Column(name = "row_version", insertable = false, updatable = false)
    private byte[] rowVersion;

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public static class CarePlanVersionStatus{
        public static String DRAFT = "DRAFT";
        public static String PENDING_APPROVAL = "PENDING_APPROVAL";
        public static String EFFECTIVE = "EFFECTIVE";
        public static String SUPERSEDED = "SUPERSEDED";
    }
    public void submitForReview(){
        this.status = CarePlanVersionStatus.PENDING_APPROVAL;
    }

    public void rejectReview(Long submmitedBy){
        this.setSubmmitedBy(submmitedBy);
        this.status =  CarePlanVersionStatus.DRAFT;
    }

    public static CarePlanVersion initFirstVersion(
                Long admissionId, Long createdBy
    ){
        CarePlanVersion c = new CarePlanVersion();
        c.setVersionNumber(1);
        c.setStatus(CarePlanVersionStatus.DRAFT);
        c.setBasedOnAdmissionId(admissionId);
        c.setCreatedBy(createdBy);
        c.setUpdatedBy(createdBy);
       return c;
    }
}

