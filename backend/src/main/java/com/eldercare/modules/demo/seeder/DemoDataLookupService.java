package com.eldercare.modules.demo.seeder;

import com.eldercare.modules.admin.facility_setup.facility.facility_layout.entity.BedEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DemoDataLookupService {

    private final JdbcTemplate jdbcTemplate;

    public Long findBedId(String facilityCode, String roomNumber, String bedNumber) {
        List<Long> bedIds = jdbcTemplate.query(
                """
                SELECT TOP 1 b.id
                FROM beds b
                INNER JOIN rooms r ON b.room_id = r.id
                INNER JOIN facilities f ON r.facility_id = f.id
                WHERE f.facility_code = ?
                  AND r.room_number = ?
                  AND b.bed_number = ?
                ORDER BY b.id
                """,
                (rs, rowNum) -> rs.getLong("id"),
                facilityCode,
                roomNumber,
                bedNumber
        );

        if (bedIds.isEmpty()) {
            throw new RuntimeException(
                    "BedEntity not found: facility_code=" + facilityCode
                            + ", room_number=" + roomNumber
                            + ", bed_number=" + bedNumber
            );
        }

        return bedIds.get(0);
    }

    public void ensureCareLevel(String careLevelCode) {
        List<Long> ids = jdbcTemplate.query(
                "SELECT id FROM care_levels WHERE level_code = ?",
                (rs, rowNum) -> rs.getLong("id"),
                careLevelCode
        );

        if (ids.isEmpty()) {
            throw new RuntimeException("Care level not found: " + careLevelCode);
        }
    }

    public Long ensureDemoUser() {
        List<Long> existing = jdbcTemplate.query(
                "SELECT id FROM users WHERE email = ?",
                (rs, rowNum) -> rs.getLong("id"),
                "demo.admin@eldercare.local"
        );

        if (!existing.isEmpty()) return existing.get(0);

        Long roleId = findSystemAdminRoleId();
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    """
                    INSERT INTO users
                    (
                        employee_code, email, password_hash, first_name, last_name,
                        phone_number, status, mfa_enabled, role_id, is_deleted,
                        created_at, updated_at
                    )
                    VALUES
                    (
                        'DEMO-ADMIN-001', 'demo.admin@eldercare.local',
                        'demo-password-hash', 'Demo', 'Admin', '9165559999',
                        'ACTIVE', 0, ?, 0, SYSDATETIMEOFFSET(), SYSDATETIMEOFFSET()
                    )
                    """,
                    Statement.RETURN_GENERATED_KEYS
            );

            ps.setLong(1, roleId);
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) throw new RuntimeException("Failed to get generated user id");

        return key.longValue();
    }

    public Long ensureIncidentSeverity() {
        List<Long> existing = jdbcTemplate.query(
                "SELECT TOP 1 id FROM incident_severities ORDER BY id",
                (rs, rowNum) -> rs.getLong("id")
        );

        if (!existing.isEmpty()) return existing.get(0);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    """
                    INSERT INTO incident_severities (level_name, chart_lock_trigger)
                    VALUES ('Low', 0)
                    """,
                    Statement.RETURN_GENERATED_KEYS
            );

            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) throw new RuntimeException("Failed to get generated incident severity id");

        return key.longValue();
    }

    private Long findSystemAdminRoleId() {
        List<Long> roleIds = jdbcTemplate.query(
                """
                SELECT TOP 1 id
                FROM roles
                WHERE role_name IN ('System_Administrator', 'System Administrator', 'NHA_Admin')
                ORDER BY id
                """,
                (rs, rowNum) -> rs.getLong("id")
        );

        return roleIds.isEmpty() ? 5L : roleIds.get(0);
    }
}
