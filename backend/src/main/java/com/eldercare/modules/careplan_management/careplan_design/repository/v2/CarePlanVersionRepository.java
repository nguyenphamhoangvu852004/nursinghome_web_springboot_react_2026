package com.eldercare.modules.careplan_management.careplan_design.repository.v2;

import com.eldercare.modules.careplan_management.careplan_design.entity.v2.CarePlanVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CarePlanVersionRepository extends JpaRepository<CarePlanVersion, Long> {

    @Query(
            value = """
                    select cpv
                    from care_plan_versions cpv
                    join care_plans cp on cpv.care_plan_id = cp.id
                    where cp.id = :care_plan_Id
                    """,
            nativeQuery = true
    )
    List<CarePlanVersion> findAllByCarePlanId(@Param("care_plan_Id") Long carePlanId);
}
