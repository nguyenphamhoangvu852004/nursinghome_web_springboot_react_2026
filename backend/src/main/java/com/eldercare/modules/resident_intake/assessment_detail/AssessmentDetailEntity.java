package com.eldercare.modules.resident_intake.assessment_detail;

import com.eldercare.modules.resident_intake.assessment.AssessmentEntity;
import com.eldercare.modules.resident_intake.assessment_metric.AssessmentMetricEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "assessment_details")
@Getter
@Setter
public class AssessmentDetailEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Integer score;
  private String notes;
  @ManyToOne
  @JoinColumn(name = "assessment_id")
  private AssessmentEntity assessment;
  @ManyToOne
  @JoinColumn(name = "metric_id")
  private AssessmentMetricEntity metric;
}
