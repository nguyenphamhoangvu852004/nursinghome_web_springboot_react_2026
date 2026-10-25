package com.eldercare.modules.admin.facility_setup.care_level.repository;

import com.eldercare.modules.admin.facility_setup.care_level.entity.CareLevelRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;



import java.util.List;

public interface CareLevelRateRepository 
        extends JpaRepository<CareLevelRateEntity, Long> {

    List<CareLevelRateEntity> findByCareLevel_Id(Long careLevelId);

}

