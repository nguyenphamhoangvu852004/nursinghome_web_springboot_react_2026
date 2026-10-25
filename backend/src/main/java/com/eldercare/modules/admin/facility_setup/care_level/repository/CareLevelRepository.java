package com.eldercare.modules.admin.facility_setup.care_level.repository;

import com.eldercare.modules.admin.facility_setup.care_level.entity.CareLevelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CareLevelRepository extends JpaRepository<CareLevelEntity, Long> {
    Optional<CareLevelEntity> findByLevelNameAndIsDeletedFalse(String levelName);
}

