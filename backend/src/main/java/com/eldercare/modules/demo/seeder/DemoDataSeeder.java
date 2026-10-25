package com.eldercare.modules.demo.seeder;

import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity;
import com.eldercare.modules.demo.dto.csv.ResidentCsvRow;
import com.eldercare.modules.demo.dto.response.SeedDemoDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DemoDataSeeder {

    private final JdbcTemplate jdbcTemplate;
    private final ResidentCsvReader residentCsvReader;
    private final DemoDataLookupService lookupService;

    public SeedDemoDataResponse seed(MultipartFile fixtureFile) {
        if (fixtureFile == null || fixtureFile.isEmpty()) {
            throw new RuntimeException("Fixture file is required");
        }

        List<ResidentCsvRow> rows = residentCsvReader.read(fixtureFile);
        Long systemUserId = lookupService.ensureDemoUser();

        int residentsLoaded = 0;
        int medicationOrdersLoaded = 0;
        int incidentsLoaded = 0;

        for (ResidentCsvRow row : rows) {
            Long bedId = lookupService.findBedId(
                    row.facilityCode(),
                    row.roomNumber(),
                    row.bedNumber()
            );

            lookupService.ensureCareLevel(row.careLevelCode());

            Long residentId = insertResident(row, bedId);
            residentsLoaded++;

            insertCarePlan(residentId);

            medicationOrdersLoaded += insertMedicationOrders(
                    residentId,
                    systemUserId,
                    row.medicationCount()
            );

            incidentsLoaded += insertIncidents(
                    residentId,
                    systemUserId,
                    row.incidentCount()
            );
        }

        return SeedDemoDataResponse.builder()
                .seedJobId(System.currentTimeMillis())
                .status("Completed")
                .residentsLoaded(residentsLoaded)
                .medicationOrdersLoaded(medicationOrdersLoaded)
                .incidentsLoaded(incidentsLoaded)
                .build();
    }

    private Long insertResident(ResidentCsvRow row, Long bedId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    """
                    INSERT INTO residents
                    (
                        first_name, middle_name, last_name, date_of_birth,
                        gender, marital_status, religion_preference, status,
                        is_chart_locked, address_id, bed_id
                    )
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0, NULL, ?)
                    """,
                    Statement.RETURN_GENERATED_KEYS
            );

            ps.setString(1, row.firstName());
            ps.setString(2, row.middleName());
            ps.setString(3, row.lastName());
            ps.setString(4, row.dateOfBirth());
            ps.setString(5, row.gender());
            ps.setString(6, row.maritalStatus());
            ps.setString(7, row.religionPreference());
            ps.setString(8, row.status());
            ps.setLong(9, bedId);

            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) throw new RuntimeException("Failed to get generated resident id");

        return key.longValue();
    }

    private void insertCarePlan(Long residentId) {
        jdbcTemplate.update(
                """
                INSERT INTO care_plans
                (status, significant_change_flag, resident_id, is_deleted, created_at, updated_at)
                VALUES ('ACTIVE', 0, ?, 0, SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET())
                """,
                residentId
        );
    }

    private int insertMedicationOrders(Long residentId, Long prescribedByUserId, int medicationCount) {
        String[][] demoMedications = {
                {"Aspirin", "81 mg", "Oral", "Once Daily", "0"},
                {"Metformin", "500 mg", "Oral", "Twice Daily", "0"},
                {"Lisinopril", "10 mg", "Oral", "Once Daily", "0"},
                {"Atorvastatin", "20 mg", "Oral", "Nightly", "0"},
                {"Warfarin", "5 mg", "Oral", "Once Daily", "1"}
        };

        int inserted = 0;

        for (int i = 0; i < medicationCount; i++) {
            String[] med = demoMedications[i % demoMedications.length];

            jdbcTemplate.update(
                    """
                    INSERT INTO medication_orders
                    (
                        resident_id, drug_name, dosage, route, frequency,
                        is_controlled_substance, status, prescribed_by,
                        is_deleted, created_at, updated_at
                    )
                    VALUES (?, ?, ?, ?, ?, ?, 'ACTIVE', ?, 0, SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET())
                    """,
                    residentId,
                    med[0],
                    med[1],
                    med[2],
                    med[3],
                    Integer.parseInt(med[4]),
                    prescribedByUserId
            );

            inserted++;
        }

        return inserted;
    }

    private int insertIncidents(Long residentId, Long reportedByUserId, int incidentCount) {
        Long severityId = lookupService.ensureIncidentSeverity();

        String[][] demoIncidents = {
                {"FALL", "OPEN", "ResidentEntity fell while walking to dining area."},
                {"MEDICATION_ERROR", "UNDER_INVESTIGATION", "Wrong medication dosage administered."},
                {"ALTERCATION", "OPEN", "Verbal altercation between two residents."},
                {"SKIN_TEAR", "CLOSED", "Skin tear on left arm during transfer."}
        };

        int inserted = 0;

        for (int i = 0; i < incidentCount; i++) {
            String[] incident = demoIncidents[i % demoIncidents.length];

            jdbcTemplate.update(
                    """
                    INSERT INTO incidents
                    (
                        incident_type, status, description, sla_deadline,
                        resident_id, severity_id, reported_by, reported_at
                    )
                    VALUES (?, ?, ?, DATEADD(HOUR, 24, SYSDATETIMEOFFSET()), ?, ?, ?, SYSDATETIMEOFFSET())
                    """,
                    incident[0],
                    incident[1],
                    incident[2],
                    residentId,
                    severityId,
                    reportedByUserId
            );

            inserted++;
        }

        return inserted;
    }
}
