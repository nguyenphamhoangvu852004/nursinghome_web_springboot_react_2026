package com.eldercare.modules.resident_intake.resident.repository;


import com.eldercare.modules.admin.facility_setup.care_level.entity.ResidentCareLevelHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


//import này 
import java.util.Optional;
@Repository
public interface ResidentCareLevelHistoryRepository extends JpaRepository<ResidentCareLevelHistoryEntity, Long> {
    List<ResidentCareLevelHistoryEntity> findByResidentId(Long residentId);

    // Tui là Hoàng Văn Sỹ - cái này nó nằm ở cái repository history care_level chứ nhỉ, tại thuộc api của tui mà
    // Lỡ rùi nên tui dùng nha
    // Từ đây
     // API 23
    List<ResidentCareLevelHistoryEntity> findByResident_Id(Long residentId);

    List<ResidentCareLevelHistoryEntity>
    findByResident_IdOrderByStartDateDesc(Long residentId);



    // API 24
    Optional<ResidentCareLevelHistoryEntity>
    findByResident_IdAndEndDateIsNull(Long residentId);



    // API 25
    // Lấy các care level đang active của resident
    // record hiện tại sẽ có endDate = null
    List<ResidentCareLevelHistoryEntity>
    findByEndDateIsNull();
    // đến đây
}

