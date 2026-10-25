package com.eldercare.modules.resident_intake.resident_profile;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "resident_sensitive_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResidentSensitiveInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private ResidentEntity resident;

    @Column(name = "ssn_encrypted")
    private String ssnEncrypted;

    @Column(name = "medical_record_number_encrypted")
    private String medicalRecordNumberEncrypted;

    @Column(name = "primary_insurance_id_encrypted")
    private String primaryInsuranceIdEncrypted;

    @Column(name = "bank_account_encrypted")
    private String bankAccountEncrypted;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private OffsetDateTime updatedAt = OffsetDateTime.now();
}
