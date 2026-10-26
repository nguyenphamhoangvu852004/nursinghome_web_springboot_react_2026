import java.nio.file.*;
import java.sql.*;
import java.time.*;
import java.util.*;

/** Manual local-development fixture runner. Requires the project's SQL Server JDBC jar. */
class CarePlanFixture {
    private static final List<String> TABLES = List.of("care_plans", "care_plan_versions", "care_goals",
            "care_interventions", "care_plan_reviews", "care_tasks");

    public static void main(String[] args) throws Exception {
        boolean apply = Arrays.asList(args).contains("--apply");
        Path fixture = Path.of("src/main/resources/db/realistic_care_plan_fixture.sql");
        if (!Files.exists(fixture)) throw new IllegalStateException("Run from the backend directory.");
        String url = System.getenv().getOrDefault("DB_URL",
                "jdbc:sqlserver://localhost:1434;databaseName=eldercare;encrypt=true;trustServerCertificate=true");
        try (Connection connection = DriverManager.getConnection(url,
                System.getenv().getOrDefault("DB_USERNAME", "sa"),
                Objects.requireNonNull(System.getenv("DB_PASSWORD"), "DB_PASSWORD must be set"))) {
            if (apply) {
                Path folder = Path.of("local-backups");
                Files.createDirectories(folder);
                Path backup = folder.resolve("care-plan-" + LocalDateTime.now().toString().replace(':', '-') + ".sql");
                String restoreSql = backup(connection);
                Files.writeString(backup, restoreSql, StandardOpenOption.CREATE_NEW);
                try (Statement restoreCheck = connection.createStatement()) {
                    restoreCheck.execute(restoreSql.replace("COMMIT TRANSACTION;", "ROLLBACK TRANSACTION;"));
                }
                System.out.println("Backup restore rehearsal passed.");
                System.out.println("Backup saved: " + backup.toAbsolutePath());
            }
            String sql = Files.readString(fixture);
            if (apply) sql = sql.replace("DECLARE @Apply bit = 0;", "DECLARE @Apply bit = 1;");
            try (Statement statement = connection.createStatement()) {
                boolean result = statement.execute(sql);
                do {
                    if (result) try (ResultSet rows = statement.getResultSet()) {
                        while (rows.next()) {
                            for (int i = 1; i <= rows.getMetaData().getColumnCount(); i++)
                                System.out.print(rows.getMetaData().getColumnLabel(i) + "=" + rows.getString(i) + " ");
                            System.out.println();
                        }
                    }
                    result = statement.getMoreResults();
                } while (result || statement.getUpdateCount() != -1);
            }
            System.out.println(apply ? "Fixture committed." : "Rehearsal passed; all changes rolled back.");
        }
    }

    // Export a runnable, transaction-protected restore script before any replacement.
    private static String backup(Connection connection) throws SQLException {
        StringBuilder sql = new StringBuilder("-- Restore the care-planning snapshot in the same local database.\n"
                + "SET NOCOUNT ON; SET XACT_ABORT ON;\nBEGIN TRY\nBEGIN TRANSACTION;\n"
                + "IF DB_NAME()<>'eldercare' THROW 51000,'Wrong database.',1;\n"
                + "DELETE FROM care_tasks;\nUPDATE care_plans SET current_version_id=NULL;\n"
                + "UPDATE care_plan_versions SET based_on_version_id=NULL,created_from_review_id=NULL;\n"
                + "DELETE FROM care_plan_reviews;\nDELETE FROM care_interventions;\nDELETE FROM care_goals;\n"
                + "DELETE FROM care_plan_versions;\nDELETE FROM care_plans;\n");
        StringBuilder links = new StringBuilder();
        for (String table : TABLES) {
            try (Statement statement = connection.createStatement(); ResultSet rows = statement.executeQuery("SELECT * FROM " + table)) {
                ResultSetMetaData meta = rows.getMetaData();
                List<Integer> columns = new ArrayList<>();
                boolean identity = false;
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    if (!Set.of("rowversion", "timestamp").contains(meta.getColumnTypeName(i).toLowerCase())) columns.add(i);
                    identity |= meta.isAutoIncrement(i);
                }
                if (identity) sql.append("SET IDENTITY_INSERT ").append(table).append(" ON;\n");
                while (rows.next()) {
                    List<String> names = new ArrayList<>(), values = new ArrayList<>();
                    for (int i : columns) {
                        String name = meta.getColumnName(i);
                        names.add("[" + name + "]");
                        Object value = rows.getObject(i);
                        boolean deferred = table.equals("care_plans") && name.equals("current_version_id")
                                || table.equals("care_plan_versions") && Set.of("based_on_version_id", "created_from_review_id").contains(name);
                        values.add(deferred ? "NULL" : literal(value));
                        if (deferred && value != null) links.append("UPDATE ").append(table).append(" SET [").append(name)
                                .append("]=").append(literal(value)).append(" WHERE id=").append(rows.getLong("id")).append(";\n");
                    }
                    sql.append("INSERT ").append(table).append(" (").append(String.join(",", names)).append(") VALUES (")
                            .append(String.join(",", values)).append(");\n");
                }
                if (identity) sql.append("SET IDENTITY_INSERT ").append(table).append(" OFF;\n");
            }
        }
        sql.append(links);
        try (Statement statement = connection.createStatement(); ResultSet rows = statement.executeQuery(
                "SELECT id,first_name,middle_name,last_name,updated_at FROM residents")) {
            while (rows.next()) sql.append("UPDATE residents SET first_name=").append(literal(rows.getObject("first_name")))
                    .append(",middle_name=").append(literal(rows.getObject("middle_name")))
                    .append(",last_name=").append(literal(rows.getObject("last_name")))
                    .append(",updated_at=").append(literal(rows.getObject("updated_at")))
                    .append(" WHERE id=").append(rows.getLong("id")).append(";\n");
        }
        return sql.append("COMMIT TRANSACTION;\nEND TRY\nBEGIN CATCH\nIF XACT_STATE()<>0 ROLLBACK TRANSACTION;\nTHROW;\nEND CATCH;\n").toString();
    }

    private static String literal(Object value) {
        if (value == null) return "NULL";
        if (value instanceof Boolean flag) return flag ? "1" : "0";
        if (value instanceof Number) return value.toString();
        if (value instanceof byte[] bytes) return "0x" + HexFormat.of().formatHex(bytes);
        return "N'" + value.toString().replace("'", "''") + "'";
    }
}
