package com.eldercare.modules.resident_intake.family_contacts;

import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "resident_contacts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResidentContactEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private ResidentEntity resident;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", nullable = false)
    private ContactEntity contact;

    @Column(name = "relationship_type", nullable = false)
    private String relationshipType;

    @Column(name = "is_guarantor", nullable = false)
    @Builder.Default
    private boolean isGuarantor = false;

    @Column(name = "is_emergency_contact", nullable = false)
    @Builder.Default
    private boolean isEmergencyContact = false;

    @Column(name = "is_primary", nullable = false)
    @Builder.Default
    private boolean isPrimary = false;

    @Column(name = "financial_responsibility_pct")
    private BigDecimal financialResponsibilityPct;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
