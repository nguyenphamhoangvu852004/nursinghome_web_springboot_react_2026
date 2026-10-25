package com.eldercare.modules.careplan_management.careplan_design.domain_models;

import com.eldercare.common.enums.CarePlanStatusEnum;
import com.eldercare.modules.careplan_management.careplan_design.entity.CareGoalEntity;
import com.eldercare.modules.careplan_management.careplan_design.entity.CarePlanEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.OffsetDateTime;

public class CarePlanEntityTests {
    @Test
    void submitForReview_ShouldChangeStatusToPendingReview_WhenStatusIsDraft() {
        // init care plan
        CarePlanEntity carePlanEntity = new CarePlanEntity();
        carePlanEntity.setStatus(CarePlanStatusEnum.DRAFT);

        // business logic
        carePlanEntity.submitForReview();

        // verify
        Assertions.assertEquals(carePlanEntity.getStatus(), CarePlanStatusEnum.PENDING_REVIEW);
        OffsetDateTime now = OffsetDateTime.now();
        Duration diff = Duration.between(carePlanEntity.getUpdatedAt(), now).abs();
        Assertions.assertTrue(
                diff.compareTo(Duration.ofSeconds(1)) < 0,
                "updatedAt should be within 1 second of now"
        );
    }

    @Test
    void submitForReview_ShouldThrowIllegalStateException_WhenStatusIsPendingReview() {
        // init care plan
        CarePlanEntity carePlanEntity = new CarePlanEntity();
        carePlanEntity.setStatus(CarePlanStatusEnum.PENDING_REVIEW);

        // businnes logic can be throw error
        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                carePlanEntity::submitForReview);

        // verify
        Assertions.assertEquals(
                "Only draft or needs update care plan can be submitted.",
                exception.getMessage()
        );
    }

    @Test
    void submitForReview_ShouldThrowIllegalStateException_WhenStatusIsActive() {
        // Arrange
        CarePlanEntity carePlanEntity = new CarePlanEntity();
        carePlanEntity.setStatus(CarePlanStatusEnum.ACTIVE);

        // Act & Assert
        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                carePlanEntity::submitForReview
        );

        Assertions.assertEquals(
                "Only draft or needs update care plan can be submitted.",
                exception.getMessage()
        );

        // Verify state is unchanged
        Assertions.assertEquals(
                CarePlanStatusEnum.ACTIVE,
                carePlanEntity.getStatus()
        );

        Assertions.assertNull(carePlanEntity.getUpdatedAt());
    }

    @Test
    void submitForReview_ShouldThrowIllegalStateException_WhenStatusIsArchived() {
        // Arrange
        CarePlanEntity carePlanEntity = new CarePlanEntity();
        carePlanEntity.setStatus(CarePlanStatusEnum.ARCHIVED);

        // Act & Assert
        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                carePlanEntity::submitForReview
        );

        Assertions.assertEquals(
                "Only draft or needs update care plan can be submitted.",
                exception.getMessage()
        );

        // Verify state is unchanged
        Assertions.assertEquals(
                CarePlanStatusEnum.ARCHIVED,
                carePlanEntity.getStatus()
        );

        Assertions.assertNull(carePlanEntity.getUpdatedAt());
    }

    @Test
    void approve_ShouldActivateCarePlan_WhenStatusIsPendingReview(){
        // init care plan
        CarePlanEntity carePlan = new CarePlanEntity();
        carePlan.setStatus(CarePlanStatusEnum.PENDING_REVIEW);

        // act
        OffsetDateTime before = OffsetDateTime.now();
        carePlan.approve("DON");
        Assertions.assertEquals(
                CarePlanStatusEnum.ACTIVE,
                carePlan.getStatus());

        // veiry
        Assertions.assertEquals(
                "DON",
                carePlan.getLastReviewBy());

        Assertions.assertNotNull(
                carePlan.getLastReviewDateTime());

        Assertions.assertFalse(
                carePlan.getUpdatedAt().isBefore(before));

        Assertions.assertFalse(
                carePlan.getLastReviewDateTime().isBefore(before));
    }

    @Test
    void approve_ShouldThrowIllegalStateException_WhenStatusIsDraft() {
        assertApproveThrowsIllegalState(CarePlanStatusEnum.DRAFT);
    }

    @Test
    void approve_ShouldThrowIllegalStateException_WhenStatusIsActive() {
        assertApproveThrowsIllegalState(CarePlanStatusEnum.ACTIVE);
    }

    @Test
    void approve_ShouldThrowIllegalStateException_WhenStatusIsReviewDue() {
        assertApproveThrowsIllegalState(CarePlanStatusEnum.REVIEW_DUE);
    }

    @Test
    void approve_ShouldThrowIllegalStateException_WhenStatusIsArchived() {
        assertApproveThrowsIllegalState(CarePlanStatusEnum.ARCHIVED);
    }

    @Test
    void reject_ShouldReturnToDraft_WhenStatusIsPendingReview() {
        // Arrange
        CarePlanEntity carePlan = new CarePlanEntity();
        carePlan.setStatus(CarePlanStatusEnum.PENDING_REVIEW);

        OffsetDateTime before = OffsetDateTime.now();

        // Act
        carePlan.reject();

        OffsetDateTime after = OffsetDateTime.now();

        // Assert
        Assertions.assertEquals(
                CarePlanStatusEnum.DRAFT,
                carePlan.getStatus());

        Assertions.assertFalse(
                carePlan.getUpdatedAt().isBefore(before));

        Assertions.assertFalse(
                carePlan.getUpdatedAt().isAfter(after));
    }

    @Test
    void reject_ShouldThrowIllegalStateException_WhenStatusIsDraft() {
        assertRejectThrowsIllegalState(CarePlanStatusEnum.DRAFT);
    }

    @Test
    void reject_ShouldThrowIllegalStateException_WhenStatusIsActive() {
        assertRejectThrowsIllegalState(CarePlanStatusEnum.ACTIVE);
    }

    @Test
    void reject_ShouldThrowIllegalStateException_WhenStatusIsArchived() {
        assertRejectThrowsIllegalState(CarePlanStatusEnum.ARCHIVED);
    }

    @Test
    void markReviewDue_ShouldChangeStatusToReviewDue_WhenStatusIsActive() {
        // Arrange
        CarePlanEntity carePlan = new CarePlanEntity();
        carePlan.setStatus(CarePlanStatusEnum.ACTIVE);

        OffsetDateTime before = OffsetDateTime.now();

        // Act
        carePlan.markReviewDue();

        OffsetDateTime after = OffsetDateTime.now();

        // Assert
        Assertions.assertEquals(
                CarePlanStatusEnum.REVIEW_DUE,
                carePlan.getStatus());

        Assertions.assertFalse(
                carePlan.getUpdatedAt().isBefore(before));

        Assertions.assertFalse(
                carePlan.getUpdatedAt().isAfter(after));
    }

    @Test
    void markReviewDue_ShouldThrowIllegalStateException_WhenStatusIsDraft() {
        assertMarkReviewDueThrowsIllegalState(CarePlanStatusEnum.DRAFT);
    }

    @Test
    void markReviewDue_ShouldThrowIllegalStateException_WhenStatusIsPendingReview() {
        assertMarkReviewDueThrowsIllegalState(CarePlanStatusEnum.PENDING_REVIEW);
    }

    @Test
    void markReviewDue_ShouldThrowIllegalStateException_WhenStatusIsArchived() {
        assertMarkReviewDueThrowsIllegalState(CarePlanStatusEnum.ARCHIVED);
    }

    @Test
    void confirmNoChanges_ShouldThrowIllegalStateException_WhenStatusIsDraft() {
        assertConfirmNoChangesThrows(CarePlanStatusEnum.DRAFT);
    }

    @Test
    void confirmNoChanges_ShouldThrowIllegalStateException_WhenStatusIsActive() {
        assertConfirmNoChangesThrows(CarePlanStatusEnum.ACTIVE);
    }

    @Test
    void confirmNoChanges_ShouldThrowIllegalStateException_WhenStatusIsPendingReview() {
        assertConfirmNoChangesThrows(CarePlanStatusEnum.PENDING_REVIEW);
    }

    private void assertApproveThrowsIllegalState(CarePlanStatusEnum status) {
        CarePlanEntity carePlan = new CarePlanEntity();
        carePlan.setStatus(status);

        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> carePlan.approve("DON")
        );

        Assertions.assertEquals(
                "This care plan is in status " + status + " that cannot be approved",
                exception.getMessage()
        );

        Assertions.assertEquals(status, carePlan.getStatus());
        Assertions.assertNull(carePlan.getLastReviewBy());
        Assertions.assertNull(carePlan.getLastReviewDateTime());
    }
    private void assertRejectThrowsIllegalState(CarePlanStatusEnum status) {
        CarePlanEntity carePlan = new CarePlanEntity();
        carePlan.setStatus(status);

        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                carePlan::reject
        );

        Assertions.assertEquals(
                "This care plan is in status " + status + " that cannot be rejected",
                exception.getMessage()
        );

        Assertions.assertEquals(status, carePlan.getStatus());
        Assertions.assertNull(carePlan.getUpdatedAt());
    }
    private void assertMarkReviewDueThrowsIllegalState(CarePlanStatusEnum status) {
        CarePlanEntity carePlan = new CarePlanEntity();
        carePlan.setStatus(status);

        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                carePlan::markReviewDue
        );

        Assertions.assertEquals(
                "This care plan is in status " + status + " that cannot be mark review due",
                exception.getMessage()
        );

        Assertions.assertEquals(status, carePlan.getStatus());
        Assertions.assertNull(carePlan.getUpdatedAt());
    }
    private void assertConfirmNoChangesThrows(CarePlanStatusEnum status) {
        CarePlanEntity carePlan = new CarePlanEntity();
        carePlan.setStatus(status);

        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> carePlan.confirmNoChanges("DON")
        );

        Assertions.assertEquals(
                "This care plan is in status " + status + " that cannot confirm no changes.",
                exception.getMessage()
        );

        Assertions.assertEquals(status, carePlan.getStatus());
    }
}

