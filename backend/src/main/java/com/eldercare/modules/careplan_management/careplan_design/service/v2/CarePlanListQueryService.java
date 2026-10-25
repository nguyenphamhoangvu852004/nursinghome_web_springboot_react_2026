package com.eldercare.modules.careplan_management.careplan_design.service.v2;

import com.eldercare.common.dto.PagedResponse;
import com.eldercare.modules.careplan_management.careplan_design.dto.v2.ListCarePlans.CarePlanListData;
import com.eldercare.modules.careplan_management.careplan_design.dto.v2.ListCarePlans.CarePlanListItem;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;

/** A flat read model: one row per plan, without serializing JPA entity graphs. */
@Service
public class CarePlanListQueryService {
    private final NamedParameterJdbcTemplate jdbc;

    public CarePlanListQueryService(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final Set<String> STATES = Set.of(
            "OPEN", "CLOSED_DISCHARGED", "CLOSED_TRANSFERRED", "CLOSED_DECEASED", "VOIDED");
    private static final Set<String> STATUSES = Set.of("DRAFT", "PENDING_APPROVAL", "EFFECTIVE", "SUPERSEDED");
    private static final Map<String, String> SORT_COLUMNS = Map.of(
            "updatedAt", "last_updated_at", "createdAt", "cp.created_at",
            "residentName", "resident_name", "id", "cp.id");

    private static final String RESIDENT_NAME = "TRIM(CONCAT(r.first_name, ' ', NULLIF(CONCAT(COALESCE(r.middle_name, ''), ' '), ' '), r.last_name))";
    private static final String LAST_UPDATED = "CASE WHEN lv.updated_at > cp.updated_at THEN lv.updated_at ELSE cp.updated_at END";
    private static final String FROM = """
            FROM care_plans cp
            LEFT JOIN residents r ON r.id = cp.resident_id
            LEFT JOIN admissions a ON a.id = cp.admission_id
            LEFT JOIN facilities f ON f.id = a.facility_id
            LEFT JOIN beds b ON b.id = r.bed_id
            LEFT JOIN rooms room ON room.id = b.room_id
            LEFT JOIN users author ON author.id = cp.created_by
            LEFT JOIN (
                SELECT v.*, ROW_NUMBER() OVER (
                    PARTITION BY care_plan_id ORDER BY version_number DESC, id DESC
                ) AS version_rank FROM care_plan_versions v
            ) lv ON lv.care_plan_id = cp.id AND lv.version_rank = 1
            LEFT JOIN care_plan_versions ev ON ev.id = cp.current_version_id
                AND ev.care_plan_id = cp.id AND ev.status = 'EFFECTIVE' AND cp.state = 'OPEN'
            """;

    @Transactional(readOnly = true)
    public PagedResponse<CarePlanListData> list(
            int page, int size, String search, String state, String versionStatus,
            String sortBy, String sortDir) {
        if (page < 0 || size < 1 || size > 100) badRequest("Page must be non-negative and size must be between 1 and 100");
        if (!SORT_COLUMNS.containsKey(sortBy) || !("ASC".equals(sortDir) || "DESC".equals(sortDir))) {
            badRequest("Unsupported sort field or direction");
        }
        if (state != null && !STATES.contains(state)) badRequest("Unsupported care plan state");
        if (versionStatus != null && !STATUSES.contains(versionStatus)) badRequest("Unsupported version status");
        String term = search == null ? "" : search.trim();
        if (term.length() > 200) badRequest("Search must be 200 characters or fewer");

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("offset", (long) page * size).addValue("size", size);
        StringBuilder where = new StringBuilder(" WHERE 1 = 1");
        if (!term.isEmpty()) {
            // Treat wildcard characters as literal search input, not LIKE operators.
            String escaped = term.replace("!", "!!").replace("%", "!%")
                    .replace("_", "!_").replace("[", "![");
            params.addValue("search", "%" + escaped + "%");
            where.append(" AND (LOWER(").append(RESIDENT_NAME).append(") LIKE LOWER(:search) ESCAPE '!'"
                    + " OR CAST(cp.id AS VARCHAR(20)) LIKE :search ESCAPE '!'"
                    + " OR CAST(cp.resident_id AS VARCHAR(20)) LIKE :search ESCAPE '!'"
                    + " OR CAST(cp.admission_id AS VARCHAR(20)) LIKE :search ESCAPE '!')");
        }
        if (state != null) {
            where.append(" AND cp.state = :state");
            params.addValue("state", state);
        }
        if (versionStatus != null) {
            where.append(" AND lv.status = :versionStatus");
            params.addValue("versionStatus", versionStatus);
        }

        CarePlanListData.Statistics statistics = jdbc.queryForObject("""
                SELECT COUNT(*) AS total,
                    COALESCE(SUM(CASE WHEN cp.state = 'OPEN' THEN 1 ELSE 0 END), 0) AS open_count,
                    COALESCE(SUM(CASE WHEN cp.state = 'OPEN' AND lv.status = 'DRAFT' THEN 1 ELSE 0 END), 0) AS draft_count,
                    COALESCE(SUM(CASE WHEN cp.state = 'OPEN' AND lv.status = 'PENDING_APPROVAL' THEN 1 ELSE 0 END), 0) AS pending_count,
                    COALESCE(SUM(CASE WHEN cp.state = 'OPEN' AND ev.id IS NOT NULL THEN 1 ELSE 0 END), 0) AS effective_count
                """ + FROM + where, params, (rs, row) -> new CarePlanListData.Statistics(
                rs.getLong("total"), rs.getLong("open_count"), rs.getLong("draft_count"),
                rs.getLong("pending_count"), rs.getLong("effective_count")));

        String select = "SELECT cp.id, cp.resident_id, " + RESIDENT_NAME + " AS resident_name, "
                + LAST_UPDATED + " AS last_updated_at, " + """
                    room.room_number, b.bed_number, cp.admission_id, a.admission_date, f.name AS facility_name,
                    cp.state, lv.id AS latest_version_id, lv.version_number AS latest_version_number,
                    lv.status AS latest_version_status, ev.id AS effective_version_id,
                    ev.version_number AS effective_version_number,
                    (SELECT COUNT(*) FROM care_goals g WHERE g.care_plan_version_id = lv.id) AS goal_count,
                    (SELECT COUNT(*) FROM care_interventions i JOIN care_goals g ON g.id = i.care_goal_id
                        WHERE g.care_plan_version_id = lv.id) AS intervention_count,
                    TRIM(CONCAT(author.first_name, ' ', author.last_name)) AS author_name,
                    cp.opened_at, cp.closed_at, cp.closed_reason, cp.created_at
                """;
        var items = jdbc.query(select + FROM + where
                + " ORDER BY " + SORT_COLUMNS.get(sortBy) + " " + sortDir + ("id".equals(sortBy) ? "" : ", cp.id DESC")
                + " OFFSET :offset ROWS FETCH NEXT :size ROWS ONLY", params,
                (rs, row) -> new CarePlanListItem(
                        rs.getLong("id"), rs.getLong("resident_id"), rs.getString("resident_name"),
                        rs.getString("room_number"), rs.getString("bed_number"), rs.getLong("admission_id"),
                        rs.getObject("admission_date", LocalDate.class), rs.getString("facility_name"), rs.getString("state"),
                        rs.getObject("latest_version_id", Long.class), rs.getObject("latest_version_number", Integer.class),
                        rs.getString("latest_version_status"), rs.getObject("effective_version_id", Long.class),
                        rs.getObject("effective_version_number", Integer.class), rs.getLong("goal_count"),
                        rs.getLong("intervention_count"), rs.getString("author_name"),
                        rs.getObject("opened_at", OffsetDateTime.class), rs.getObject("closed_at", OffsetDateTime.class),
                        rs.getString("closed_reason"), rs.getObject("created_at", OffsetDateTime.class),
                        rs.getObject("last_updated_at", OffsetDateTime.class)));
        long total = statistics.total();
        int pages = (int) ((total + size - 1) / size);
        return PagedResponse.of(new CarePlanListData(items, statistics), 200, "Success", page, pages, size, total);
    }

    private static void badRequest(String message) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
