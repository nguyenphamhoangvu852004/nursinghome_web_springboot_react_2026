package com.eldercare.modules.resident_intake.assessment_metric;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "assessment_metrics")
@Getter
@Setter
public class AssessmentMetricEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String category; // ADL, IADL, BRADEN, MORSE
  private String metricName;
}
