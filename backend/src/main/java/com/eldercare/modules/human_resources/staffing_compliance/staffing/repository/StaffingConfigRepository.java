package com.eldercare.modules.human_resources.staffing_compliance.staffing.repository;

import com.eldercare.modules.human_resources.staffing_compliance.StaffingConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffingConfigRepository
        extends JpaRepository<StaffingConfigEntity, Long> {
}
