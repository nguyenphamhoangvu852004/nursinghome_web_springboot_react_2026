package com.eldercare;

import com.eldercare.modules.resident_intake.resident.dto.request.*;
import com.eldercare.modules.resident_intake.resident.dto.response.*;
import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity;
import com.eldercare.modules.admin.facility_setup.facility.facility_layout.entity.BedEntity;
import com.eldercare.modules.admin.facility_setup.facility.facility_layout.entity.RoomEntity;
import com.eldercare.modules.admin.facility_setup.facility.facility_layout.repository.BedRepository;
import com.eldercare.modules.admin.facility_setup.facility.facility_layout.repository.RoomRepository;
import com.eldercare.modules.resident_intake.resident.repository.ResidentRepository;
import com.eldercare.modules.resident_intake.resident.service.ResidentService;
import com.eldercare.modules.admin.facility_setup.facility.facility_profile.entity.FacilityEntity;
import com.eldercare.modules.admin.facility_setup.facility.facility_profile.repository.FacilityRepository;
import com.eldercare.modules.resident_intake.admission_ledger.repository.AdmissionRepository;
import com.eldercare.modules.resident_intake.resident.repository.ResidentCareLevelHistoryRepository;
import com.eldercare.modules.resident_intake.resident.repository.ResidentSensitiveInfoRepository;
import com.eldercare.common.enums.RoomType;
import com.eldercare.common.enums.BedStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ResidentV1ServiceTests {

    @Autowired
    private ResidentService residentService;

    @Autowired
    private ResidentRepository residentRepository;

    @Autowired
    private BedRepository bedRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private AdmissionRepository admissionRepository;

    @Autowired
    private ResidentCareLevelHistoryRepository residentCareLevelHistoryRepository;

    @Autowired
    private ResidentSensitiveInfoRepository residentSensitiveInfoRepository;

    private ResidentEntity testResident;
    private BedEntity bed1;
    private BedEntity bed2;

    @BeforeEach
    void setUp() {
        admissionRepository.deleteAll();
        residentCareLevelHistoryRepository.deleteAll();
        residentSensitiveInfoRepository.deleteAll();
        residentRepository.deleteAll();
        bedRepository.deleteAll();
        roomRepository.deleteAll();

        FacilityEntity facility = facilityRepository.findById(1L)
                .orElseGet(() -> {
                    FacilityEntity f = new FacilityEntity();
                    f.setFacilityCode("FAC-001");
                    f.setName("Default Facility");
                    f.setLicenseNumber("LIC-12345");
                    f.setTargetState("CA");
                    return facilityRepository.save(f);
                });

        RoomEntity room = new RoomEntity();
        room.setRoomNumber("101");
        room.setRoomType(RoomType.SEMI_PRIVATE);
        room.setFacility(facility);
        room = roomRepository.save(room);

        bed1 = new BedEntity();
        bed1.setBedNumber("A");
        bed1.setStatus(BedStatus.AVAILABLE);
        bed1.setRoom(room);
        bed1 = bedRepository.save(bed1);

        bed2 = new BedEntity();
        bed2.setBedNumber("B");
        bed2.setStatus(BedStatus.AVAILABLE);
        bed2.setRoom(room);
        bed2 = bedRepository.save(bed2);

        testResident = new ResidentEntity();
        testResident.setFirstName("John");
        testResident.setLastName("Doe");
        testResident.setDateOfBirth(LocalDate.of(1950, 1, 1));
        testResident.setGender("MALE");
        testResident.setStatus("ACTIVE");
        testResident.setBed(bed1);
        testResident.setChartLocked(false);
        testResident = residentRepository.save(testResident);
    }

    @Test
    void testGetResidentsV1() {
        ResidentListResponseContainerDto container = residentService.getResidentsV1(null, null, "John", 1, 10);
        assertNotNull(container);
        assertEquals(1, container.getResidents().size());
        assertEquals("John", container.getResidents().get(0).getFirstName());
    }

    @Test
    void testGetResidentByIdV1() {
        ResidentResponseDto dto = residentService.getResidentByIdV1(testResident.getId());
        assertNotNull(dto);
        assertEquals("John", dto.getFirstName());
    }

    @Test
    void testCreateResidentV1() {
        ResidentCreateRequestDto dto = new ResidentCreateRequestDto();
        dto.setFirstName("Susan");
        dto.setLastName("Harris");
        dto.setDateOfBirth(LocalDate.of(1948, 11, 23));
        dto.setGender("FEMALE");

        ResidentResponseDto created = residentService.createResidentV1(dto);
        assertNotNull(created);
        assertEquals("Susan", created.getFirstName());
        assertEquals("PENDING", created.getStatus());
        assertFalse(created.getIsChartLocked());
    }

    @Test
    void testUpdateResidentV1() {
        ResidentUpdateRequestDto dto = new ResidentUpdateRequestDto();
        dto.setFirstName("Johnny");

        ResidentResponseDto updated = residentService.updateResidentV1(testResident.getId(), dto);
        assertNotNull(updated);
        assertEquals("Johnny", updated.getFirstName());
    }

    @Test
    void testUpdateResidentStatusV1() {
        ResidentStatusUpdateRequestDto dto = new ResidentStatusUpdateRequestDto();
        dto.setStatus("DISCHARGED");
        dto.setReason("Discharged home");

        ResidentResponseDto updated = residentService.updateResidentStatusV1(testResident.getId(), dto);
        assertNotNull(updated);
        assertEquals("DISCHARGED", updated.getStatus());

        // Verify bed assignment was cleared
        ResidentEntity dbResident = residentRepository.findById(testResident.getId()).orElseThrow();
        assertNull(dbResident.getBed());
    }

    @Test
    void testAssignResidentBedV1() {
        ResidentAssignBedRequestDto dto = new ResidentAssignBedRequestDto();
        dto.setBedId(bed2.getId());

        ResidentResponseDto updated = residentService.assignResidentBedV1(testResident.getId(), dto);
        assertNotNull(updated);
        assertEquals(bed2.getId(), updated.getBedId());
    }

    @Test
    void testAssignResidentBedOccupiedConflict() {
        // Create second active resident in bed2
        ResidentEntity other = new ResidentEntity();
        other.setFirstName("Bob");
        other.setLastName("Smith");
        other.setDateOfBirth(LocalDate.of(1955, 5, 5));
        other.setStatus("ACTIVE");
        other.setBed(bed2);
        residentRepository.save(other);

        // Try to assign bob's bed (bed2) to testResident
        ResidentAssignBedRequestDto dto = new ResidentAssignBedRequestDto();
        dto.setBedId(bed2.getId());

        assertThrows(IllegalStateException.class, () -> {
            residentService.assignResidentBedV1(testResident.getId(), dto);
        });
    }

    @Test
    void testLockAndUnlockChartV1() {
        ResidentChartLockRequestDto lockDto = new ResidentChartLockRequestDto();
        lockDto.setReason("Audit complete");

        ResidentResponseDto locked = residentService.lockResidentChartV1(testResident.getId(), lockDto);
        assertTrue(locked.getIsChartLocked());

        // When locked, clinical updates are blocked
        ResidentUpdateRequestDto updateDto = new ResidentUpdateRequestDto();
        updateDto.setFirstName("LockedUpdate");

        assertThrows(IllegalStateException.class, () -> {
            residentService.updateResidentV1(testResident.getId(), updateDto);
        });

        // Unlock chart
        ResidentChartLockRequestDto unlockDto = new ResidentChartLockRequestDto();
        unlockDto.setReason("Correcting entry");

        ResidentResponseDto unlocked = residentService.unlockResidentChartV1(testResident.getId(), unlockDto);
        assertFalse(unlocked.getIsChartLocked());
    }
}
