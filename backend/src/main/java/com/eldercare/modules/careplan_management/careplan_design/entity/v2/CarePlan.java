package com.eldercare.modules.careplan_management.careplan_design.entity.v2;


import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

@Builder
@RequiredArgsConstructor()
@AllArgsConstructor()
@Getter
@Setter
@ToString
@Entity
@Table(name = "care_plans")
public class CarePlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resident_id", nullable = false)
    private Long residentId;

    @Column(name = "admission_id",nullable = false)
    private Long admissionId;

    @Column(name = "current_version_id",nullable = true)
    private Long currentVersionId = null;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_version_id", insertable = false, updatable = false)
    @JsonIgnore
    @ToString.Exclude
    private CarePlanVersion currentVersion;

    @OneToMany(mappedBy = "carePlan", fetch = FetchType.LAZY)
    @Builder.Default
    @JsonIgnore
    @ToString.Exclude
    private List<CarePlanVersion> versions = new ArrayList<>();

    @Column
    private String state = null;

    @Column(name = "opened_at")
    private OffsetDateTime openedAt=OffsetDateTime.now();

    @Column(name = "closed_at")
    private OffsetDateTime closedAt = null;

    @Column(name = "closed_reason")
    private String closedReason = null;

    @Column(name = "created_at")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "created_by")
    private Long createdBy = null;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @Column(name = "updated_by")
    private Long updatedBy = null;

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

    public static class CarePlanState{
        public static String OPEN = "OPEN";
        public static String CLOSED_DISCHARGED = "CLOSED_DISCHARGED";
        public static String CLOSED_TRANSFERRED = "CLOSED_TRANSFERRED";
        public static String CLOSED_DECEASED = "CLOSED_DECEASED";
        public static String VOIDED = "VOIDED";
    }

    public CarePlan update(CarePlan newCarePlan){
        this.setResidentId(newCarePlan.getResidentId());
        this.setAdmissionId(newCarePlan.getAdmissionId());
        this.setCurrentVersionId(newCarePlan.getCurrentVersionId());
        this.setState(newCarePlan.getState());
        this.setOpenedAt(newCarePlan.getOpenedAt());
        this.setClosedAt(newCarePlan.getClosedAt());
        this.setClosedReason(newCarePlan.getClosedReason());
        this.setCreatedAt(newCarePlan.getCreatedAt());
        this.setCreatedBy(newCarePlan.getCreatedBy());
        this.setUpdatedAt(newCarePlan.getUpdatedAt());
        this.setUpdatedBy(newCarePlan.getUpdatedBy());
        return this;
    }

    public static CarePlan create(Long residentId, Long admissionId,Long creatorId){
        CarePlan c = new CarePlan();
        if (residentId == null) throw new IllegalArgumentException("residentId must not be null");
        if (admissionId == null) throw new IllegalArgumentException("admissionId must not be null");
        if (creatorId == null) throw new IllegalArgumentException("creatorId must not be null");
        c.setAdmissionId(admissionId);
        c.setResidentId(residentId);
        c.setCreatedBy(creatorId);
        c.setUpdatedBy(creatorId);
        c.setState(CarePlanState.OPEN);
        c.setCurrentVersion(CarePlanVersion.initFirstVersion(admissionId,creatorId));
        System.out.println(c);
        return c;
    }

    public static CarePlan create(Long residentId, Long admissionId,Long creatorId,Long currentVersionId){
        CarePlan c = new CarePlan();
        if (residentId == null) throw new IllegalArgumentException("residentId must not be null");
        if (admissionId == null) throw new IllegalArgumentException("admissionId must not be null");
        if (creatorId == null) throw new IllegalArgumentException("creatorId must not be null");
        if (currentVersionId == null) throw new IllegalArgumentException("currentVersionId must not be null");
        c.setAdmissionId(admissionId);
        c.setResidentId(residentId);
        c.setCreatedBy(creatorId);
        c.setUpdatedBy(creatorId);
        c.setState(CarePlanState.OPEN);
        System.out.println(c);
        return c;
    }



    public CarePlanVersion prepareInitialVersionForPersistence() {
        if (id == null) throw new IllegalStateException("CarePlan must be persisted before its initial version");
        if (currentVersion == null || currentVersionId != null) {
            throw new IllegalStateException("CarePlan must have an unpersisted initial version");
        }
        currentVersion.setCarePlanId(id);
        currentVersion.setCarePlan(this);
        return currentVersion;
    }

    public void linkPersistedCurrentVersion(CarePlanVersion version) {
        Objects.requireNonNull(version, "Version is required");
        if (id == null || version.getId() == null || !id.equals(version.getCarePlanId())) {
            throw new IllegalArgumentException("Persisted version must belong to this CarePlan");
        }
        this.currentVersion = version;
        this.currentVersionId = version.getId();
    }

    public Boolean isOpened(){
        return Objects.equals(this.state, CarePlanState.OPEN);
    }
}
