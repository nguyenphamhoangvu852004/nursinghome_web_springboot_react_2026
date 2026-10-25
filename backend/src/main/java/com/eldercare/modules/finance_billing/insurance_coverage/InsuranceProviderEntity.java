package com.eldercare.modules.finance_billing.insurance_coverage;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "insurance_providers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsuranceProviderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider_name", nullable = false)
    private String providerName;

    @Column(name = "provider_type", nullable = false)
    private String providerType;
}
