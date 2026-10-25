package com.eldercare.modules.resident_intake.pre_admission;

import java.time.OffsetDateTime;

import com.eldercare.modules.admin.user_management.UserEntity;
import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity;

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
@Table(name = "pre_admission_screenings")
@Getter
@Setter
public class PreAdmissionScreeningEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String status; // DRAFT, COMPLETED, REJECTED
  @ManyToOne
  @JoinColumn(name = "resident_id")
  private ResidentEntity resident;
  @ManyToOne
  @JoinColumn(name = "screened_by")
  private UserEntity screenedBy;
  private Boolean isCurrent;
  private OffsetDateTime createdAt;
}
