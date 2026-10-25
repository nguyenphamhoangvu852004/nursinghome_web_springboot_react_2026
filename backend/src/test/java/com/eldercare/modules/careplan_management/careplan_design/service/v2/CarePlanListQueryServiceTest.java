package com.eldercare.modules.careplan_management.careplan_design.service.v2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CarePlanListQueryServiceTest {
    private CarePlanListQueryService service;

    @BeforeEach
    void setup() {
        var source = new DriverManagerDataSource("jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MSSQLServer;DB_CLOSE_DELAY=-1", "sa", "");
        var jdbc = new JdbcTemplate(source);
        service = new CarePlanListQueryService(new NamedParameterJdbcTemplate(source));
        jdbc.execute("CREATE TABLE residents (id BIGINT PRIMARY KEY, first_name VARCHAR(100), middle_name VARCHAR(100), last_name VARCHAR(100), bed_id BIGINT)");
        jdbc.execute("CREATE TABLE admissions (id BIGINT PRIMARY KEY, admission_date DATE, facility_id BIGINT)");
        jdbc.execute("CREATE TABLE facilities (id BIGINT PRIMARY KEY, name VARCHAR(100))");
        jdbc.execute("CREATE TABLE beds (id BIGINT PRIMARY KEY, bed_number VARCHAR(20), room_id BIGINT)");
        jdbc.execute("CREATE TABLE rooms (id BIGINT PRIMARY KEY, room_number VARCHAR(20))");
        jdbc.execute("CREATE TABLE users (id BIGINT PRIMARY KEY, first_name VARCHAR(100), last_name VARCHAR(100))");
        jdbc.execute("""
                CREATE TABLE care_plans (id BIGINT PRIMARY KEY, resident_id BIGINT, admission_id BIGINT,
                    current_version_id BIGINT, state VARCHAR(30), created_by BIGINT,
                    opened_at TIMESTAMP WITH TIME ZONE, closed_at TIMESTAMP WITH TIME ZONE,
                    closed_reason VARCHAR(500), created_at TIMESTAMP WITH TIME ZONE, updated_at TIMESTAMP WITH TIME ZONE)
                """);
        jdbc.execute("""
                CREATE TABLE care_plan_versions (id BIGINT PRIMARY KEY, care_plan_id BIGINT,
                    version_number INT, status VARCHAR(30), updated_at TIMESTAMP WITH TIME ZONE)
                """);
        jdbc.execute("CREATE TABLE care_goals (id BIGINT PRIMARY KEY, care_plan_version_id BIGINT)");
        jdbc.execute("CREATE TABLE care_interventions (id BIGINT PRIMARY KEY, care_goal_id BIGINT)");
        jdbc.update("INSERT INTO residents VALUES (1,'Ada',NULL,'Lovelace',1),(2,'Bea','May','Jones',NULL)");
        jdbc.update("INSERT INTO facilities VALUES (1,'Main facility')");
        jdbc.update("INSERT INTO admissions VALUES (1,'2026-01-01',1)");
        jdbc.update("INSERT INTO beds VALUES (1,'A',1)");
        jdbc.update("INSERT INTO rooms VALUES (1,'101')");
        jdbc.update("INSERT INTO users VALUES (1,'Demo','Admin')");
        jdbc.update("""
                INSERT INTO care_plans (id,resident_id,admission_id,current_version_id,state,created_by,created_at,updated_at)
                VALUES (1,1,1,11,'OPEN',1,'2026-01-01 00:00:00+00','2026-01-01 00:00:00+00'),
                       (2,2,1,NULL,'OPEN',1,'2026-01-01 00:00:00+00','2026-01-01 00:00:00+00'),
                       (3,2,1,31,'CLOSED_DISCHARGED',1,'2026-01-01 00:00:00+00','2026-01-01 00:00:00+00'),
                       (4,999,1,NULL,'OPEN',NULL,'2026-01-01 00:00:00+00','2026-01-01 00:00:00+00'),
                       (5,2,1,11,'OPEN',1,'2026-01-01 00:00:00+00','2026-01-01 00:00:00+00')
                """);
        jdbc.update("""
                INSERT INTO care_plan_versions VALUES
                    (11,1,1,'EFFECTIVE','2026-01-01 00:00:00+00'),
                    (12,1,2,'DRAFT','2026-01-02 00:00:00+00'),
                    (13,1,1,'SUPERSEDED','2026-01-01 00:00:00+00'),
                    (21,2,1,'PENDING_APPROVAL','2026-01-01 00:00:00+00'),
                    (31,3,1,'DRAFT','2026-01-01 00:00:00+00')
                """);
        jdbc.update("INSERT INTO care_goals VALUES (1,12),(2,12),(3,11)");
        jdbc.update("INSERT INTO care_interventions VALUES (1,1),(2,1),(3,2),(4,3)");
    }

    @Test
    void latestVersionAndContentCountsDoNotDuplicatePlanRows() {
        var response = service.list(0,10,"Ada",null,null,"updatedAt","DESC");
        assertEquals(1, response.getData().list().size());
        var item = response.getData().list().getFirst();
        assertEquals("Ada Lovelace", item.residentName());
        assertEquals(12L, item.latestVersionId());
        assertEquals(2, item.latestVersionNumber());
        assertEquals("DRAFT", item.latestVersionStatus());
        assertEquals(11L, item.effectiveVersionId());
        assertEquals(2, item.goalCount());
        assertEquals(3, item.interventionCount());
        assertEquals("101", item.roomNumber());
        assertEquals("Demo Admin", item.createdByName());
        assertEquals(2, item.updatedAt().getDayOfMonth());
    }

    @Test
    void statisticsUseAllFilteredPlansRatherThanCurrentPage() {
        var response = service.list(1,2,null,null,null,"id","ASC");
        assertEquals(2, response.getData().list().size());
        assertEquals(3, response.getData().list().getFirst().id());
        assertEquals(5, response.getMetadata().getTotalElements());
        assertEquals(3, response.getMetadata().getTotalPage());
        assertEquals(2, response.getMetadata().getCurrentPage());
        var stats = response.getData().statistics();
        assertEquals(5, stats.total());
        assertEquals(4, stats.open());
        assertEquals(1, stats.draft());
        assertEquals(1, stats.pendingApproval());
        assertEquals(1, stats.effective());
    }

    @Test
    void stateAndVersionFiltersComposeAndUseLatestVersionOnly() {
        var response = service.list(0,10,null,"OPEN","DRAFT","id","DESC");
        assertEquals(1, response.getData().statistics().total());
        assertEquals(1, response.getData().list().getFirst().id());
        assertEquals(0, service.list(0,10,null,null,"SUPERSEDED","id","DESC").getData().list().size());
    }

    @Test
    void missingRelationsAndVersionsRemainVisibleAndForeignVersionIsNotEffective() {
        var items = service.list(0,10,null,null,null,"id","ASC").getData().list();
        assertNull(items.get(2).effectiveVersionId()); // Closed plan with a draft pointer.
        assertNull(items.get(3).latestVersionId());
        assertEquals(0, items.get(3).goalCount());
        assertNull(items.get(4).effectiveVersionId()); // Pointer belongs to another plan.
    }

    @Test
    void emptyAndLiteralWildcardSearchesHaveAccurateZeroStatistics() {
        for (String search : new String[]{"%", "_", "[", "' OR 1=1 --", "does-not-exist"}) {
            var response = service.list(0,10,search,null,null,"residentName","ASC");
            assertTrue(response.getData().list().isEmpty());
            assertEquals(0, response.getData().statistics().total());
            assertEquals(0, response.getData().statistics().draft());
        }
    }

    @Test
    void invalidFiltersAndSortCannotBeInterpolatedIntoSql() {
        assertEquals(400, assertThrows(ResponseStatusException.class,
                () -> service.list(-1,10,null,null,null,"id","ASC")).getStatusCode().value());
        assertThrows(ResponseStatusException.class, () -> service.list(0,101,null,null,null,"id","ASC"));
        assertThrows(ResponseStatusException.class, () -> service.list(0,10,null,"ACTIVE",null,"id","ASC"));
        assertThrows(ResponseStatusException.class, () -> service.list(0,10,null,null,"ACTIVE","id","ASC"));
        assertThrows(ResponseStatusException.class, () -> service.list(0,10,null,null,null,"id; DROP TABLE users","ASC"));
        assertThrows(ResponseStatusException.class, () -> service.list(0,10,null,null,null,"id","DESC; --"));
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "CARE_PLAN_QUERY_JDBC_URL", matches = ".+")
    void sqlServerReadOnlySmokeTest() {
        var source = new DriverManagerDataSource(System.getenv("CARE_PLAN_QUERY_JDBC_URL"),
                System.getenv("CARE_PLAN_QUERY_DB_USER"), System.getenv("CARE_PLAN_QUERY_DB_PASSWORD"));
        var live = new CarePlanListQueryService(new NamedParameterJdbcTemplate(source));
        var sql = new JdbcTemplate(source);
        long count = sql.queryForObject("SELECT COUNT(*) FROM care_plans", Long.class);
        var response = live.list(0,10,null,null,null,"updatedAt","DESC");
        assertEquals(count, response.getMetadata().getTotalElements());
        assertEquals(count, response.getData().statistics().total());
        assertEquals(Math.min(count,10), response.getData().list().size());
        assertEquals(response.getData().list().size(), response.getData().list().stream().map(item -> item.id()).distinct().count());
        for (var item : response.getData().list()) {
            assertEquals(sql.queryForObject("SELECT COUNT(*) FROM care_goals WHERE care_plan_version_id = ?", Long.class,
                    item.latestVersionId()), item.goalCount());
            if (item.effectiveVersionId() != null) {
                assertEquals("OPEN", item.state());
                assertEquals("EFFECTIVE", sql.queryForObject("SELECT status FROM care_plan_versions WHERE id = ?", String.class,
                        item.effectiveVersionId()));
            }
        }
        var openDrafts = live.list(0,10,null,"OPEN","DRAFT","residentName","ASC");
        assertTrue(openDrafts.getData().list().stream().allMatch(item ->
                "OPEN".equals(item.state()) && "DRAFT".equals(item.latestVersionStatus())));
        assertTrue(live.list(0,10,"' OR 1=1 --",null,null,"id","DESC").getData().list().isEmpty());
    }
}
