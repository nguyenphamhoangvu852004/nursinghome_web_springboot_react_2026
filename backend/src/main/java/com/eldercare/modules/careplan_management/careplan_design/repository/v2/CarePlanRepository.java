package com.eldercare.modules.careplan_management.careplan_design.repository.v2;

import com.eldercare.modules.careplan_management.careplan_design.entity.v2.CarePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CarePlanRepository extends JpaRepository<CarePlan, Long> {
    @Query(
            value = """
                    select cp.*
                      from care_plans cp
                      join admissions a on cp.admission_id = a.id
                      where a.id = :admissionId
                    """,
            nativeQuery = true
    )
    List<CarePlan> findAllByAdmissionId(@Param("admissionId") Long admissionId);
}
