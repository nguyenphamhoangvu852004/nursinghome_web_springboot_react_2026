package com.eldercare.modules.resident_intake.assessment;

import java.time.OffsetDateTime;
import java.util.List;

import com.eldercare.modules.admin.user_management.UserEntity;
import com.eldercare.modules.resident_intake.admission_ledger.AdmissionEntity;
import com.eldercare.modules.resident_intake.assessment_detail.AssessmentDetailEntity;
import com.eldercare.modules.admin.facility_setup.care_level.entity.CareLevelEntity;
import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "assessments")
@Getter
@Setter
public class AssessmentEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Integer adlTotalScore;
  private Boolean isOverridden;
  private String overrideReason;
  private String status;
  @ManyToOne
  @JoinColumn(name = "suggested_care_level_id")
  private CareLevelEntity suggestedCareLevel;
  @ManyToOne
  @JoinColumn(name = "confirmed_care_level_id")
  private CareLevelEntity confirmedCareLevel;
  @ManyToOne
  @JoinColumn(name = "resident_id")
  private ResidentEntity resident;
  @ManyToOne
  @JoinColumn(name = "assessed_by")
  private UserEntity assessedBy;
  @ManyToOne
  @JoinColumn(name = "admission_id")
  private AdmissionEntity admission;
  private Boolean isCurrent;
  private OffsetDateTime createdAt;
  @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  private List<AssessmentDetailEntity> details;
}