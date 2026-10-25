package com.eldercare.modules.careplan_management.careplan_design.service.v2;

import com.eldercare.exception.NotFoundException;
import com.eldercare.modules.careplan_management.careplan_design.dto.v2.CreateCarePlanVersion.CreateCarePlanVersionRequest;
import com.eldercare.modules.careplan_management.careplan_design.entity.v2.CarePlanVersion;
import com.eldercare.modules.careplan_management.careplan_design.repository.v2.CarePlanVersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class CarePlanVersionService {
    private final CarePlanVersionRepository carePlanVersionRepository;

    public CarePlanVersionService(CarePlanVersionRepository carePlanVersionRepository) {
        this.carePlanVersionRepository = carePlanVersionRepository;
    }

    public Object createNewVersion(Object inputData){
        CreateCarePlanVersionRequest input = (CreateCarePlanVersionRequest) inputData;
        CarePlanVersion carePlanVersion =  new CarePlanVersion();
        carePlanVersion.setEffectiveFrom(input.effectiveFrom());
        carePlanVersion.setEffectiveTo(input.effectiveTo());
        carePlanVersion.setCarePlanId(input.carePlanId());
        carePlanVersion.setBasedOnAdmissionId(input.basedOnAssessmentId());
        carePlanVersion.setBasedOnVersionId(input.basedOnVersionId());
        carePlanVersion.setContentHash(input.contentHash().getBytes());
        carePlanVersion.setChangeSummary(input.changeSummary());
        carePlanVersion.setCreatedFromReviewId(input.createdFromReviewId());
        carePlanVersion.setStatus(CarePlanVersion.CarePlanVersionStatus.DRAFT);

        carePlanVersion = this.carePlanVersionRepository.save(carePlanVersion);
        return carePlanVersion;
    }

    public CarePlanVersion createInitVersion(Long careplanId,Long admissionId,Long creatorId){
        java.time.OffsetDateTime now = OffsetDateTime.now();

        CarePlanVersion carePlanVersion = CarePlanVersion.builder()
                .carePlanId(careplanId)
                .versionNumber(1)
                .status(CarePlanVersion.CarePlanVersionStatus.DRAFT)
                .basedOnVersionId(null)
                .basedOnAdmissionId(admissionId)
                .createdAt(now)
                .updatedAt(now)
                .createdBy(creatorId)
                .updatedBy(creatorId)
                .approvedAt(null)
                .approvedby(null)
                .changeSummary(null)
                .effectiveFrom(null)
                .effectiveTo(null)
                .createdFromReviewId(null)
                .goals(null)
                .rejectedAt(null)
                .rejectedBy(null)
                .rejectionReason(null)
                .build();

        return this.carePlanVersionRepository.save(carePlanVersion);
    };

    @Transactional
    public CarePlanVersion approve(Long versionId, Long actorId) {
        Objects.requireNonNull(actorId, "Actor id is required");
        CarePlanVersion version = findById(versionId);
        checkPendingApproval(version);

        version.setStatus(CarePlanVersion.CarePlanVersionStatus.EFFECTIVE);
        version.setApprovedby(actorId);
        version.setApprovedAt(OffsetDateTime.now());
        version.setRejectedBy(null);
        version.setRejectedAt(null);
        version.setRejectionReason(null);
        version.setUpdatedBy(actorId);
        return carePlanVersionRepository.save(version);
    }

    @Transactional
    public CarePlanVersion reject(Long versionId, Long actorId, String rejectReason) {
        Objects.requireNonNull(actorId, "Actor id is required");
        if (rejectReason == null || rejectReason.isBlank() || rejectReason.length() > 1000) {
            throw new IllegalArgumentException("Rejection reason is required and must not exceed 1000 characters");
        }
        CarePlanVersion version = findById(versionId);
        checkPendingApproval(version);

        version.rejectReview(actorId);
        version.setRejectedBy(actorId);
        version.setRejectedAt(OffsetDateTime.now());
        version.setRejectionReason(rejectReason.trim());
        version.setApprovedby(null);
        version.setApprovedAt(null);
        version.setUpdatedBy(actorId);
        return carePlanVersionRepository.save(version);
    }

    private void checkPendingApproval(CarePlanVersion version) {
        if (!CarePlanVersion.CarePlanVersionStatus.PENDING_APPROVAL.equals(version.getStatus())) {
            throw new IllegalStateException("Care Plan Version must be pending approval: " + version.getId());
        }
    }

    public List<CarePlanVersion> getCarePlanVersionsOfCarePlan(Long carePlanId){
        return this.carePlanVersionRepository.findAllByCarePlanId(carePlanId);
    }

    public CarePlanVersion findById(Long versionId) {
        return this.carePlanVersionRepository.findById(versionId).orElseThrow(()-> new NotFoundException("Not found Care Plan Version: " + versionId));

    }

    protected void checkValidState(Long versionId){

    }
}
