package com.eldercare.modules.risk_incident.sla_rules.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.eldercare.modules.risk_incident.sla_rules.entity.SLAConfigEntity;
import java.util.Optional;

@Repository
public interface SLAConfigRepository extends JpaRepository<SLAConfigEntity, Long> {
        Optional<SLAConfigEntity> findBySeverityId(Long severityId);


}
