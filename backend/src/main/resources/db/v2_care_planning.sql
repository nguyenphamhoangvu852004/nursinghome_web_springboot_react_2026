-- Schema recreation only: this script drops the care-planning tables.
-- For development data replacement use realistic_care_plan_fixture.sql and scripts/CarePlanFixture.java.
USE eldercare;



DROP TABLE IF EXISTS care_interventions;

DROP TABLE IF EXISTS care_plan_versions;

DROP TABLE IF EXISTS care_goals;

DROP TABLE IF EXISTS care_plans;

DROP TABLE IF EXISTS care_plan_reviews;

-- v2
CREATE TABLE care_plans (
    id BIGINT IDENTITY (1, 1) NOT NULL,
    resident_id BIGINT NOT NULL,
    admission_id BIGINT DEFAULT 2 NOT NULL,
    current_version_id BIGINT,
    state VARCHAR (20) DEFAULT 'VOIDED' CONSTRAINT CHK_care_plan_state CHECK (state IN ('OPEN', 'CLOSED_DISCHARGED', 'CLOSED_TRANSFERRED', 'CLOSED_DECEASED', 'VOIDED')) NULL,
    opened_at DATETIMEOFFSET (7) DEFAULT SYSDATETIMEOFFSET() NOT NULL,
    closed_at DATETIMEOFFSET (7),
    closed_reason VARCHAR (500),
    created_at DATETIMEOFFSET (7) DEFAULT SYSDATETIMEOFFSET() NOT NULL,
    created_by BIGINT,
    updated_at DATETIMEOFFSET (7) DEFAULT SYSDATETIMEOFFSET() NOT NULL,
    updated_by BIGINT,
    row_version ROWVERSION NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE care_plan_versions (
    id BIGINT IDENTITY (1, 1) NOT NULL,
    care_plan_id BIGINT NOT NULL,
    version_number INT,
    based_on_version_id BIGINT,
    based_on_admission_id BIGINT,
    created_from_review_id BIGINT,
    status varchar(20) not null constraint chk_care_plan_version_status check (status in ('DRAFT','PENDING_APPROVAL','EFFECTIVE','SUPERSEDED')) default 'DRAFT',
    change_summary VARCHAR (500),
    submitted_at DATETIMEOFFSET (7),
    submitted_by BIGINT,
    approved_at DATETIMEOFFSET (7),
    approved_by BIGINT,
    rejected_at DATETIMEOFFSET (7),
    rejected_by BIGINT,
    rejection_reason VARCHAR (1000),
    effective_from DATETIMEOFFSET (7),
    effective_to DATETIMEOFFSET (7),
    content_hash VARBINARY (32),
    created_at DATETIMEOFFSET (7) DEFAULT SYSDATETIMEOFFSET() NOT NULL,
    created_by BIGINT,
    updated_at DATETIMEOFFSET (7) DEFAULT SYSDATETIMEOFFSET() NOT NULL,
    updated_by BIGINT,
    row_version ROWVERSION NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE care_goals (
    id BIGINT IDENTITY (1, 1) NOT NULL,
    care_plan_version_id BIGINT NOT NULL,
    problem_or_need NVARCHAR (1000) NOT NULL, -- ex: High fall risk during transfer
    title NVARCHAR (255) NOT NULL, -- ex: Safe bed-to-chair transfer
    desired_outcome NVARCHAR (1000) NOT NULL, -- ex: Resident transfers without fall
    baseline_value NVARCHAR (2000) NOT NULL, -- ex: Requires two-person assist
    target_value NVARCHAR (255), -- ex: One-person assist
    target_unit VARCHAR (50), --ex: assist_level
    measurement_method NVARCHAR (500), --ex: Review transfer tasks for 7 days
    target_date DATE, --ex: 2026-11-15
    priority VARCHAR (20) DEFAULT 'LOW' NOT NULL CONSTRAINT CHK_priority CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),
    status VARCHAR (20) DEFAULT 'ACTIVE' NOT NULL CONSTRAINT chk_status CHECK (status IN ('ACTIVE', 'ACHIEVED', 'DISCONTINUED')),
    resident_preference NVARCHAR (1000), --ex: Prefers transfer after breakfast
    created_at DATETIMEOFFSET (7) DEFAULT SYSDATETIMEOFFSET() NOT NULL,
    created_by BIGINT,
    updated_at DATETIMEOFFSET (7) DEFAULT SYSDATETIMEOFFSET() NOT NULL,
    updated_by BIGINT,
    PRIMARY KEY (id)
);

CREATE TABLE care_interventions (
    id BIGINT IDENTITY (1, 1) NOT NULL,
    care_goal_id BIGINT NOT NULL,
    title NVARCHAR (255) NOT NULL,
    instructions NVARCHAR (2000) NOT NULL, --ex: huong dan cu the
    responsible_discipline VARCHAR (50), --ex:	Discipline chịu trách nhiệm clinical - NURSING
    assigned_role VARCHAR (50) NOT NULL, -- ex: Role thường thực hiện. CNA
    frequency_code VARCHAR (30) NOT NULL, --ex : Q2H
    schedule_definition NVARCHAR (1000), -- JSON/cấu trúc lịch đã version hóa và UI có thể giải thích. ex: {"times":["08:00","12:00","18:00"]}
    start_date DATE, --ex: 2026-10-15 
    end_date DATE, --ex: 2026-11-15
    prn_criteria NVARCHAR (1000), -- ex: When resident requests toileting assistance
    evidence_or_rationale NVARCHAR (1000), -- desc: Lý do clinical/assessment source. ex: Morse fall score indicates high risk
    requires_task_generation BIT DEFAULT 1 NOT NULL, -- desc: Phân biệt hướng dẫn thuần túy với intervention phải sinh task.ex: 1
    sort_order INT NOT NULL, -- desc: Thứ tự hiển thị dưới goal. ex: 10
    created_at DATETIMEOFFSET (7) DEFAULT SYSDATETIMEOFFSET() NOT NULL,
    created_by BIGINT,
    updated_at DATETIMEOFFSET (7) DEFAULT SYSDATETIMEOFFSET() NOT NULL,
    updated_by BIGINT,
    PRIMARY KEY (id)
);

CREATE TABLE care_plan_reviews (
    id BIGINT IDENTITY (1, 1) NOT NULL,
    care_plan_id BIGINT NOT NULL,
    review_type VARCHAR (50) NOT NULL CONSTRAINT chk_review_type CHECK (review_type IN ('ADMISSION', 'QUARTERLY', 'ANNUAL', 'SIGNIFICANT_CHANGE', 'SIGNIFICANT_CORRECTION_COMPREHENSIVE', 'SIGNIFICANT_CORRECTION_QUARTERLY', 'OTHER_CLINICAL')),
    status VARCHAR (30) NOT NULL,
    source_assessment_id BIGINT,
    previous_version_id BIGINT NOT NULL,
    resulting_version_id BIGINT,
    trigger_source VARCHAR (50) NOT NULL,
    triggered_at DATETIMEOFFSET (7) DEFAULT SYSDATETIMEOFFSET() NOT NULL,
    determined_at DATETIMEOFFSET (7),
    scheduled_due_at DATETIMEOFFSET (7),
    started_at DATETIMEOFFSET (7),
    completed_at DATETIMEOFFSET (7),
    material_change BIT, -- decs: 	NULL chưa quyết định; 0 giữ version; 1 tạo candidate version. ex: 1
    significant_change_reason NVARCHAR (2000), -- desc: Bắt buộc với significant change.: ex: New decline in transfer ability and continence
    decision_notes NVARCHAR (2000), -- desc:  Lý do kết luận, kể cả no-change. ex:Current interventions remain appropriate
    created_at DATETIMEOFFSET (7),
    created_by BIGINT,
    completed_by BIGINT,
    row_version ROWVERSION NOT NULL,
    PRIMARY KEY (id)
);

-- The old unlinked 900001/910001 fixture and ad-hoc UPDATE statements were removed.
-- realistic_care_plan_fixture.sql uses existing resident/admission pairs and validates all relationships.


use eldercare;

select * from care_goals;