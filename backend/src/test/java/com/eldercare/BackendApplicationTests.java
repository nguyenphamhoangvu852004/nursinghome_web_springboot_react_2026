package com.eldercare;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

@SpringBootTest
class BackendApplicationTests {

	@Autowired
	private DataSource dataSource;

	@Test
	void contextLoads() throws Exception {
		try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
			String[] tables = {"residents", "rooms", "beds", "addresses", "contacts", "resident_contacts", "resident_insurance_policies", "insurance_providers", "admissions"};
			for (String table : tables) {
				System.out.println("=== DATA FROM TABLE: " + table + " ===");
				try (ResultSet rs = stmt.executeQuery("SELECT TOP 5 * FROM " + table)) {
					ResultSetMetaData md = rs.getMetaData();
					int colCount = md.getColumnCount();
					while (rs.next()) {
						StringBuilder sb = new StringBuilder();
						for (int i = 1; i <= colCount; i++) {
							sb.append(md.getColumnName(i)).append("=").append(rs.getObject(i)).append(", ");
						}
						System.out.println(sb.toString());
					}
				} catch (Exception e) {
					System.out.println("Error reading table " + table + ": " + e.getMessage());
				}
			}
		}
	}

}
