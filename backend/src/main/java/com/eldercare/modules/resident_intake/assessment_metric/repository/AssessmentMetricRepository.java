package com.eldercare.modules.resident_intake.assessment_metric.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eldercare.modules.resident_intake.assessment_metric.AssessmentMetricEntity;

public interface AssessmentMetricRepository extends JpaRepository<AssessmentMetricEntity, Long> {
}
