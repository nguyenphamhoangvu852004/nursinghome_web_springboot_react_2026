package com.eldercare.modules.careplan_management.cna_daily_tasks.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** Read-only regression check for the versioned goal -> plan relationship on SQL Server. */
@EnabledIfEnvironmentVariable(named = "CARE_PLAN_QUERY_JDBC_URL", matches = ".+")
class CareTaskRepositoryQueryTest {
    @Test
    void groupingAndPaginationResolveResidentsThroughTheVersionedCarePlan() {
        var source = new DriverManagerDataSource(System.getenv("CARE_PLAN_QUERY_JDBC_URL"),
                System.getenv("CARE_PLAN_QUERY_DB_USER"), System.getenv("CARE_PLAN_QUERY_DB_PASSWORD"));
        var factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(source);
        factory.setPackagesToScan("com.eldercare");
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        factory.setJpaPropertyMap(Map.of("hibernate.hbm2ddl.auto", "none", "hibernate.show_sql", "false"));
        factory.afterPropertiesSet();
        EntityManager em = factory.getObject().createEntityManager();
        try {
            var repository = new JpaRepositoryFactory(em).getRepository(CareTaskRepository.class);
            var sql = new JdbcTemplate(source);
            long expected = sql.queryForObject("""
                    SELECT COUNT(*) FROM care_tasks t
                    JOIN care_interventions i ON i.id=t.care_intervention_id
                    JOIN care_goals g ON g.id=i.care_goal_id
                    JOIN care_plan_versions v ON v.id=g.care_plan_version_id
                    JOIN care_plans p ON p.id=v.care_plan_id
                    JOIN residents r ON r.id=p.resident_id
                    """, Long.class);
            var grouped = repository.findEnrichedTasksForGrouping(null, null, null, null, null, null, null);
            assertEquals(expected, grouped.size());
            var page = repository.searchEnrichedTasks(null, null, null, null, null, null, null, PageRequest.of(0, 10));
            assertEquals(expected, page.getTotalElements());
            for (var task : page.getContent()) {
                assertEquals(sql.queryForObject("SELECT resident_id FROM care_plans WHERE id=?", Long.class,
                        task.getCarePlanId()), task.getResidentId());
                assertNotNull(task.getResidentDisplayName());
                assertFalse(task.getResidentDisplayName().isBlank());
            }
        } finally {
            em.close();
            factory.destroy();
        }
    }
}
