# Realistic Care plan development fixture

All clinical scenarios are fictional demonstration records. They are not clinical orders.

The SQL fixture replaces `care_plans`, `care_plan_versions`, `care_goals`,
`care_interventions`, `care_plan_reviews` and `care_tasks` in the local `eldercare`
database. It selects existing active residents and their latest current admission;
it does not invent resident/admission IDs. Explicit `Resident... VisualDemo` and
`Eleanor Demo` placeholder names on participating profiles are replaced with
fictional names. Other resident fields and other modules are preserved.

The current development database has 36 eligible residents. The fixture creates
36 plans, 54 versions, 162 goals, 324 interventions and 18 reviews. Ten care domains
cover mobility, falls, nutrition/hydration, skin, medicine/pain monitoring,
cognition, continence, breathing/activity tolerance, swallowing/oral care and
sleep/social engagement. Each resident has a primary domain and a related need.

Workflow examples include initial drafts, pending initial approvals, effective
plans, revisions awaiting approval while the previous version remains effective,
and approved revisions with superseded history. All current admissions remain
open; discharge or death is not fabricated. Reviews link to existing completed
assessments where available.

CNA tasks span two days before today through two days after today in UTC+07:00.
They are derived from the current effective version's explicit schedule, respect
start/end dates and weekday restrictions, and include completed, missed, pending,
abnormal and unassigned examples. PRN and event-only interventions do not create
invented scheduled tasks. Nurses' interventions are not assigned to CNAs.
Task counts vary with the date and weekday of the run.

## Run

Use JDK 21+ and the SQL Server JDBC jar from the backend dependency cache.
Run from `backend/`, supplying your local JDBC jar path:

```bash
export CARE_FIXTURE_JDBC_JAR=/absolute/path/to/mssql-jdbc-13.4.0.jre11.jar
# Rehearsal: insert, validate, then roll back every row change.
java -cp "$CARE_FIXTURE_JDBC_JAR" scripts/CarePlanFixture.java
# Export a restorable snapshot, validate restoration by rollback, then replace data.
java -cp "$CARE_FIXTURE_JDBC_JAR" scripts/CarePlanFixture.java --apply
```

`DB_PASSWORD` is required. Optional environment variables are `DB_URL` and
`DB_USERNAME`. Their defaults match the local development SQL Server settings (`localhost:1434`,
database `eldercare`). Run against a development database with no concurrent
writers. The runner is manual and never runs on application startup.

`local-backups/care-plan-<timestamp>.sql` is saved before mutation and ignored by
Git. To restore, open that snapshot in your SQL Server client connected to the
same database and execute it. It restores all six care tables and the original
resident names. It intentionally regenerates SQL Server rowversion values.
Identity values can advance during rollback rehearsals; do not depend on fixed IDs.

The SQL file defaults to `@Apply = 0` (rollback). Direct manual application requires
an independent backup and changing that switch to 1. Any relationship or timeline
validation failure rolls back the replacement.

`v2_care_planning.sql` now contains schema recreation only. Its old 40-plan fixture
with nonexistent resident/admission IDs and exploratory updates has been removed.
The Care tasks JPA relation and grouping queries use the v2 path
`intervention -> goal -> version -> plan -> resident`.
