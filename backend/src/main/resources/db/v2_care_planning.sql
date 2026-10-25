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

SET NOCOUNT ON;

SET XACT_ABORT ON;

DECLARE @ResidentIdStart AS BIGINT = 900001;

DECLARE @AdmissionIdStart AS BIGINT = 910001;

DECLARE @ActorId AS BIGINT = 1;

DECLARE @SeedAnchor AS DATETIMEOFFSET (7) = '2026-10-12T08:00:00+07:00';

BEGIN TRY
    BEGIN TRANSACTION;
    IF OBJECT_ID(N'dbo.care_plans', N'U') IS NULL
        THROW 50001, 'Required table dbo.care_plans does not exist.', 1;
    IF OBJECT_ID(N'dbo.care_plan_versions', N'U') IS NULL
        THROW 50002, 'Required table dbo.care_plan_versions does not exist.', 1;
    IF OBJECT_ID(N'dbo.care_goals', N'U') IS NULL
        THROW 50003, 'Required table dbo.care_goals does not exist.', 1;
    IF OBJECT_ID(N'dbo.care_interventions', N'U') IS NULL
        THROW 50004, 'Required table dbo.care_interventions does not exist.', 1;
    IF EXISTS (SELECT 1
               FROM   dbo.care_plans
               WHERE  resident_id BETWEEN @ResidentIdStart AND @ResidentIdStart + 39
                      AND admission_id BETWEEN @AdmissionIdStart AND @AdmissionIdStart + 39)
        THROW 50005, 'Seed resident/admission range already exists. Change the ID start values or remove the prior seed data.', 1;
    DECLARE @GoalTemplates TABLE (
        domain_no           INT             NOT NULL,
        goal_no             INT             NOT NULL,
        problem_or_need     NVARCHAR (1000) NOT NULL,
        title               NVARCHAR (255)  NOT NULL,
        desired_outcome     NVARCHAR (1000) NOT NULL,
        baseline_value      NVARCHAR (2000) NOT NULL,
        target_value        NVARCHAR (255)  NULL,
        target_unit         VARCHAR (50)    NULL,
        measurement_method  NVARCHAR (500)  NULL,
        priority            VARCHAR (20)    NOT NULL,
        resident_preference NVARCHAR (1000) NULL,
        PRIMARY KEY (domain_no, goal_no));
    INSERT  INTO @GoalTemplates (domain_no, goal_no, problem_or_need, title, desired_outcome, baseline_value, target_value, target_unit, measurement_method, priority, resident_preference)
    VALUES                     (1, 1, N'High fall risk during transfers and ambulation.', N'Safe transfers', N'Resident will complete bed-to-chair and toilet transfers without a fall or near-fall for 30 consecutive days.', N'Requires two-person assistance and gives inconsistent safety cues.', N'One-person assist with no falls', 'assist_level', N'Review transfer documentation and incident reports weekly.', 'HIGH', N'Prefers transfers after breakfast and wants staff to explain each step.'),
    (1, 2, N'Unsteady gait and inconsistent use of mobility aid.', N'Safe supervised ambulation', N'Resident will walk the planned distance with the prescribed mobility aid and no loss of balance on at least 5 of 7 days.', N'Walks 10 metres with a walker and frequent cueing.', N'30 metres with standby assist', 'metres', N'Review mobility task records and therapy notes each week.', 'HIGH', N'Prefers walking in the quiet corridor before lunch.'),
    (2, 1, N'Reduced lower-extremity strength after deconditioning.', N'Improve functional mobility', N'Resident will participate in the mobility programme on at least 5 days per week and maintain current transfer ability.', N'Needs moderate assistance to stand and tires after two minutes.', N'5 sessions per week', 'sessions_per_week', N'Count completed mobility sessions and compare transfer-assistance level.', 'MEDIUM', N'Enjoys exercises accompanied by familiar music.'),
    (2, 2, N'Risk of contracture due to limited active range of motion.', N'Maintain joint range of motion', N'Resident will maintain pain-free functional range of motion through the next review.', N'Limited shoulder and knee range; passive exercises tolerated.', N'No measurable decline', 'range_of_motion', N'Nursing and therapy range-of-motion assessment every two weeks.', 'MEDIUM', N'Prefers shorter sessions with rest between limbs.'),
    (3, 1, N'Unintentional weight loss and reduced meal intake.', N'Stabilise nutrition', N'Resident will consume at least 75 percent of offered meals and maintain weight within 1 kg for four weeks.', N'Consumes approximately 50 percent of meals and lost 2 kg this month.', N'75 percent meal intake', 'percent', N'Review meal-intake chart daily and weight weekly.', 'HIGH', N'Prefers warm breakfast foods and small frequent portions.'),
    (3, 2, N'Risk of dehydration related to low spontaneous fluid intake.', N'Maintain hydration', N'Resident will meet the prescribed daily fluid target unless clinically contraindicated.', N'Drinks approximately 900 mL daily without prompting.', N'1500 mL per day', 'ml_per_day', N'Total fluid balance at the end of each shift.', 'HIGH', N'Prefers chilled water and tea served in a handled cup.'),
    (4, 1, N'High risk of pressure injury due to immobility and moisture.', N'Protect skin integrity', N'Resident will remain free from new pressure injuries through the next review.', N'Braden score indicates high risk; sacral skin is intact with intermittent redness.', N'No new pressure injury', 'skin_event', N'Complete skin inspection each shift and weekly Braden reassessment.', 'HIGH', N'Prefers side-lying positions and requests privacy during skin care.'),
    (4, 2, N'Chronic lower-leg dryness with risk of skin breakdown.', N'Maintain healthy lower-leg skin', N'Lower-leg skin will remain intact with reduced dryness and no fissures within 30 days.', N'Dry, flaky skin on both lower legs without open areas.', N'Intact moisturised skin', 'skin_condition', N'Document skin condition during daily personal care.', 'MEDIUM', N'Prefers unscented moisturiser.'),
    (5, 1, N'Multiple medicines and risk of adverse effects.', N'Safe medication administration', N'Resident will receive prescribed medicines safely with adverse effects identified and escalated promptly.', N'Requires staff administration and monitoring for dizziness and sedation.', N'100 percent safe administration', 'percent', N'Review medication administration record and adverse-effect observations daily.', 'HIGH', N'Prefers medicines one at a time with water after food when permitted.'),
    (5, 2, N'Chronic pain affects sleep and participation in care.', N'Improve pain control', N'Resident will report pain at or below 3 out of 10 within one hour of planned or PRN intervention.', N'Pain commonly reaches 6 out of 10 during morning care.', N'Pain score at or below 3', 'pain_score', N'Compare pain score before and after intervention.', 'HIGH', N'Prefers heat pack and repositioning before PRN medicine when appropriate.'),
    (6, 1, N'Moderate cognitive impairment causes disorientation and anxiety.', N'Reduce distress from disorientation', N'Resident will respond to reassurance and orientation cues with reduced distress during most episodes.', N'Becomes anxious in late afternoon and repeatedly asks to go home.', N'Distress settles within 15 minutes', 'minutes', N'Record triggers, response, and time to settle on behaviour chart.', 'MEDIUM', N'Responds well to family photographs and being addressed by preferred name.'),
    (6, 2, N'Risk of unsafe wandering, especially during evening hours.', N'Support safe purposeful walking', N'Resident will walk safely in supervised areas without exit-seeking incidents for 30 days.', N'Attempts to enter restricted areas two to three times per week.', N'Zero unsafe exit events', 'events', N'Review behaviour and safety-event records weekly.', 'HIGH', N'Enjoys purposeful walks to the garden and lounge.'),
    (7, 1, N'Requires assistance with bathing, dressing, and grooming.', N'Maximise independence in personal care', N'Resident will complete at least two personal-care steps independently on 5 of 7 days.', N'Currently waits for staff to complete most personal-care steps.', N'Two independent steps', 'adl_steps', N'Record assistance level in daily ADL documentation.', 'MEDIUM', N'Prefers a shower after breakfast and chooses clothing the night before.'),
    (7, 2, N'Fatigue limits participation in morning care.', N'Pace daily care activities', N'Resident will complete essential morning care without excessive fatigue on at least 5 days per week.', N'Needs prolonged rest after bathing and dressing.', N'Fatigue at or below 3 of 10', 'fatigue_score', N'Ask resident to rate fatigue after morning care.', 'LOW', N'Prefers care delivered slowly with a seated rest after bathing.'),
    (8, 1, N'Urinary urgency and functional incontinence.', N'Maintain continence routine', N'Resident will use the toilet or commode according to schedule with no more than one incontinent episode per day.', N'Two to three urinary incontinent episodes daily.', N'At most one episode per day', 'episodes_per_day', N'Review continence chart every 24 hours.', 'MEDIUM', N'Prefers toileting assistance from same-gender staff when available.'),
    (8, 2, N'Risk of constipation related to reduced mobility and medicine use.', N'Promote regular bowel pattern', N'Resident will have a comfortable bowel movement at least every three days without straining.', N'Bowel movement every four to five days with occasional straining.', N'At least one every 3 days', 'bowel_frequency', N'Review bowel chart daily and assess abdominal symptoms.', 'MEDIUM', N'Prefers warm prune juice with breakfast.'),
    (9, 1, N'Dysphagia creates aspiration risk during meals and drinks.', N'Safe swallowing', N'Resident will eat and drink using prescribed strategies without coughing, choking, or wet voice at 90 percent of observed meals.', N'Occasional cough with thin fluids and rapid eating.', N'90 percent safe meals', 'percent', N'Observe swallowing signs and review meal notes daily.', 'HIGH', N'Prefers upright dining at the table and unhurried meals.'),
    (9, 2, N'Reduced oral health may affect comfort and food intake.', N'Maintain oral comfort', N'Resident will have a clean, moist, pain-free mouth with oral care completed twice daily.', N'Dry mouth and plaque are present; needs setup and cueing.', N'Twice-daily oral care', 'times_per_day', N'Daily oral-care record and weekly mouth assessment.', 'MEDIUM', N'Prefers a soft toothbrush and mild mint toothpaste.'),
    (10, 1, N'Poor sleep continuity with frequent night waking.', N'Improve restorative sleep', N'Resident will sleep for at least five cumulative hours overnight on 5 of 7 nights.', N'Usually sleeps three to four hours and wakes repeatedly.', N'Five hours per night', 'hours', N'Review sleep chart each morning.', 'MEDIUM', N'Prefers a warm drink, dim lighting, and quiet music before bed.'),
    (10, 2, N'Social withdrawal and reduced participation in meaningful activity.', N'Increase meaningful engagement', N'Resident will participate in one preferred individual or group activity on at least four days per week.', N'Usually remains in room and joins one activity weekly.', N'Four activity days per week', 'days_per_week', N'Review activity participation and resident feedback weekly.', 'LOW', N'Enjoys gardening, old music, and small groups rather than large events.');
    DECLARE @InterventionTemplates TABLE (
        domain_no                INT             NOT NULL,
        goal_no                  INT             NOT NULL,
        intervention_no          INT             NOT NULL,
        title                    NVARCHAR (255)  NOT NULL,
        instructions             NVARCHAR (2000) NOT NULL,
        responsible_discipline   VARCHAR (50)    NULL,
        assigned_role            VARCHAR (50)    NOT NULL,
        frequency_code           VARCHAR (30)    NOT NULL,
        schedule_definition      NVARCHAR (1000) NULL,
        prn_criteria             NVARCHAR (1000) NULL,
        evidence_or_rationale    NVARCHAR (1000) NULL,
        requires_task_generation BIT             NOT NULL,
        PRIMARY KEY (domain_no, goal_no, intervention_no));
    INSERT  INTO @InterventionTemplates (domain_no, goal_no, intervention_no, title, instructions, responsible_discipline, assigned_role, frequency_code, schedule_definition, prn_criteria, evidence_or_rationale, requires_task_generation)
    VALUES                             (1, 1, 1, N'Assist every transfer', N'Use gait belt and prescribed transfer aid. Confirm footwear, lock wheels, explain each step, and document assistance level.', 'NURSING', 'CNA', 'EACH_TRANSFER', N'{"event":"transfer"}', NULL, N'High fall risk and inconsistent safety awareness.', 1),
    (1, 1, 2, N'Complete transfer safety check', N'Check environment, call bell, bed height, brakes, and mobility aid before leaving the resident.', 'NURSING', 'CNA', 'QSHIFT', N'{"times":["06:00","14:00","22:00"]}', NULL, N'Environmental controls reduce preventable transfer falls.', 1),
    (1, 2, 1, N'Supervised walker programme', N'Walk with resident using prescribed walker and standby or hands-on assistance; stop for dizziness, pain, or unsafe gait.', 'THERAPY', 'CNA', 'DAILY', N'{"times":["10:30"]}', NULL, N'Graded supervised walking supports mobility while controlling fall risk.', 1),
    (1, 2, 2, N'Review gait and aid suitability', N'Assess gait, endurance, and correct setup of mobility aid; update nursing instructions when needs change.', 'PHYSIOTHERAPY', 'PHYSIOTHERAPIST', 'WEEKLY', N'{"days":["MON"],"times":["13:30"]}', N'After a fall, near-fall, or clear mobility decline.', N'Regular reassessment ensures the aid and assistance level remain appropriate.', 1),
    (2, 1, 1, N'Supported sit-to-stand exercises', N'Complete the prescribed sit-to-stand set with rest periods and close guarding. Do not continue if symptomatic.', 'PHYSIOTHERAPY', 'CNA', 'DAILY', N'{"times":["09:30"]}', NULL, N'Progressive functional exercise supports transfer strength.', 1),
    (2, 1, 2, N'Monitor activity tolerance', N'Observe breathlessness, pain, dizziness, and fatigue before and after mobility activity; escalate a significant change.', 'NURSING', 'NURSE', 'EACH_SESSION', N'{"event":"mobility_session"}', N'Any new chest pain, marked breathlessness, dizziness, or functional decline.', N'Deconditioned residents need symptom monitoring during graded activity.', 1),
    (2, 2, 1, N'Provide range-of-motion programme', N'Perform prescribed active-assisted or passive range-of-motion exercises slowly within comfort.', 'THERAPY', 'CNA', 'BID', N'{"times":["10:00","16:00"]}', NULL, N'Regular movement helps maintain joint range and comfort.', 1),
    (2, 2, 2, N'Position limbs in neutral alignment', N'Use prescribed supports and check that joints are aligned without pressure or pain.', 'NURSING', 'CNA', 'Q4H', N'{"intervalHours":4}', N'After repositioning, return from activity, or reported discomfort.', N'Consistent positioning reduces contracture and pressure risk.', 1),
    (3, 1, 1, N'Provide fortified preferred meals', N'Offer dietitian-approved fortified foods in small portions, assist as needed, and record percentage consumed.', 'DIETETICS', 'CNA', 'TID_MEALS', N'{"times":["08:00","12:00","18:00"]}', NULL, N'Energy-dense preferred food supports intake and weight stability.', 1),
    (3, 1, 2, N'Monitor weekly weight', N'Weigh under consistent conditions and notify nurse or dietitian of loss greater than 1 kg or ongoing intake below target.', 'NURSING', 'NURSE', 'WEEKLY', N'{"days":["MON"],"times":["07:30"]}', N'Earlier if oedema, acute illness, or visible nutritional decline occurs.', N'Trending weight and intake identifies nutritional deterioration early.', 1),
    (3, 2, 1, N'Offer preferred fluids', N'Offer prescribed fluids in the preferred cup, prompt gently, and record intake unless a restriction applies.', 'NURSING', 'CNA', 'Q2H', N'{"intervalHours":2,"start":"08:00","end":"20:00"}', N'After activity, toileting, fever, vomiting, or resident request.', N'Frequent supported offers improve hydration in residents with low thirst drive.', 1),
    (3, 2, 2, N'Review hydration status', N'Review fluid total, urine pattern, oral moisture, and clinical signs; escalate intake below target or dehydration concerns.', 'NURSING', 'NURSE', 'QSHIFT', N'{"times":["06:00","14:00","22:00"]}', NULL, N'Shift review supports early recognition of dehydration.', 1),
    (4, 1, 1, N'Reposition and offload pressure', N'Reposition using the documented turning plan, float heels when in bed, and confirm pressure-relieving equipment is correctly placed.', 'NURSING', 'CNA', 'Q2H', N'{"intervalHours":2}', N'Sooner for discomfort, moisture, or visible pressure redness.', N'Immobility and high Braden risk require scheduled pressure redistribution.', 1),
    (4, 1, 2, N'Inspect high-risk skin areas', N'Inspect sacrum, heels, hips, elbows, and device contact points; document findings and report non-blanching redness immediately.', 'NURSING', 'NURSE', 'QSHIFT', N'{"times":["06:00","14:00","22:00"]}', NULL, N'Frequent inspection supports early pressure-injury detection.', 1),
    (4, 2, 1, N'Apply prescribed moisturiser', N'Clean and dry lower legs gently, then apply unscented moisturiser; do not apply between toes.', 'NURSING', 'CNA', 'BID', N'{"times":["09:00","19:00"]}', NULL, N'Routine emollient care reduces dryness and fissuring.', 1),
    (4, 2, 2, N'Monitor lower-leg skin', N'Check for cracks, redness, warmth, swelling, drainage, or pain and report new findings.', 'NURSING', 'NURSE', 'DAILY', N'{"times":["09:00"]}', N'Immediately if open skin, spreading redness, or acute swelling is observed.', N'Early escalation reduces complications from skin breakdown.', 1),
    (5, 1, 1, N'Administer medicines safely', N'Follow medication rights, current orders, allergy status, swallowing instructions, and required observations. Record administration and omissions.', 'NURSING', 'NURSE', 'MED_PASS', N'{"event":"scheduled_medication_round"}', NULL, N'Polypharmacy increases administration and adverse-effect risk.', 1),
    (5, 1, 2, N'Monitor medicine effects', N'Observe for dizziness, sedation, bleeding, bowel change, or other medicine-specific effects and escalate according to policy.', 'NURSING', 'NURSE', 'QSHIFT', N'{"times":["06:00","14:00","22:00"]}', N'After a new medicine, dose change, fall, or suspected adverse reaction.', N'Active surveillance helps identify medicine-related harm.', 1),
    (5, 2, 1, N'Assess and respond to pain', N'Assess pain location, quality, and score; provide prescribed pharmacological or non-pharmacological care and reassess within one hour.', 'NURSING', 'NURSE', 'QSHIFT_PRN', N'{"times":["06:00","14:00","22:00"],"prn":true}', N'On resident request, non-verbal pain signs, or before painful care.', N'Paired assessment and reassessment are required to judge effectiveness.', 1),
    (5, 2, 2, N'Use comfort measures', N'Offer repositioning, support, heat if ordered, relaxation, or distraction according to resident preference and contraindications.', 'NURSING', 'CNA', 'PRN', NULL, N'Pain, stiffness, or discomfort reported or observed.', N'Preferred non-drug measures can reduce pain and improve participation.', 1),
    (6, 1, 1, N'Provide calm orientation cues', N'Approach from the front, use preferred name, explain the immediate activity, and offer one simple cue at a time. Avoid arguing.', 'NURSING', 'CNA', 'EACH_CONTACT', N'{"event":"care_contact"}', NULL, N'Consistent calm communication reduces distress linked to cognitive impairment.', 0),
    (6, 1, 2, N'Document distress triggers and response', N'Record antecedent, behaviour, intervention, and outcome for significant distress; notify nurse of a new or escalating pattern.', 'NURSING', 'CNA', 'PRN', NULL, N'For distress, agitation, repeated calling out, or marked change from baseline.', N'Pattern tracking supports person-centred prevention and clinical review.', 1),
    (6, 2, 1, N'Offer purposeful supervised walk', N'Invite resident to walk a familiar safe route, provide conversation or a simple purpose, and redirect gently from restricted areas.', 'ACTIVITIES', 'CNA', 'BID', N'{"times":["10:30","17:00"]}', N'When pacing or exit-seeking begins.', N'Purposeful movement meets the need to walk while reducing unsafe wandering.', 1),
    (6, 2, 2, N'Maintain wandering safety controls', N'Check identification, footwear, environment, and approved alert measures; complete location checks according to risk plan.', 'NURSING', 'CNA', 'QSHIFT', N'{"times":["06:00","14:00","22:00"]}', NULL, N'Environmental and identification controls reduce harm if wandering occurs.', 1),
    (7, 1, 1, N'Cue resident through personal care', N'Set up supplies, offer choices, give one-step cues, and allow time for the resident to complete capable steps before assisting.', 'NURSING', 'CNA', 'DAILY', N'{"times":["08:30"]}', NULL, N'Graded assistance preserves function, dignity, and autonomy.', 1),
    (7, 1, 2, N'Document ADL assistance level', N'Record the help required for bathing, dressing, grooming, and hygiene and report sustained decline.', 'NURSING', 'CNA', 'DAILY', N'{"times":["10:00"]}', NULL, N'Consistent ADL tracking identifies functional change.', 1),
    (7, 2, 1, N'Pace morning care', N'Break care into manageable steps, provide a seated rest after bathing, and stop if symptoms or marked fatigue develop.', 'NURSING', 'CNA', 'DAILY', N'{"times":["08:30"]}', NULL, N'Energy conservation supports participation without excessive fatigue.', 1),
    (7, 2, 2, N'Assess unexplained fatigue', N'Review fatigue pattern, sleep, intake, pain, vital signs as indicated, and report new or worsening fatigue.', 'NURSING', 'NURSE', 'PRN', NULL, N'New fatigue, inability to complete usual care, or associated clinical symptoms.', N'New functional fatigue may indicate an acute clinical change.', 1),
    (8, 1, 1, N'Provide prompted toileting', N'Offer toilet or commode on schedule, ensure safe transfer, allow privacy, and record continence outcome.', 'NURSING', 'CNA', 'Q3H', N'{"intervalHours":3,"start":"07:00","end":"22:00"}', N'On request, restlessness, or other individual continence cues.', N'Prompted toileting reduces functional incontinence episodes.', 1),
    (8, 1, 2, N'Protect skin after incontinence', N'Cleanse promptly with pH-balanced product, pat dry, apply barrier product as ordered, and check skin.', 'NURSING', 'CNA', 'PRN', NULL, N'After each incontinent episode.', N'Prompt moisture management reduces incontinence-associated dermatitis.', 1),
    (8, 2, 1, N'Support bowel routine', N'Offer prescribed fluids, fibre-containing choices, mobility, and unhurried toileting at the resident-preferred time.', 'NURSING', 'CNA', 'DAILY', N'{"times":["08:30"]}', NULL, N'Consistent routine supports regular bowel function.', 1),
    (8, 2, 2, N'Review bowel chart and escalate', N'Review bowel record, stool consistency, discomfort, and prescribed bowel protocol; notify nurse of trigger criteria.', 'NURSING', 'NURSE', 'DAILY', N'{"times":["14:00"]}', N'No bowel movement for three days, vomiting, marked distension, pain, or blood.', N'Early action can prevent faecal impaction and complications.', 1),
    (9, 1, 1, N'Apply swallowing precautions', N'Seat fully upright, provide prescribed texture and fluid thickness, use small amounts and slow pace, and keep upright after meals.', 'SPEECH_THERAPY', 'CNA', 'TID_MEALS', N'{"times":["08:00","12:00","18:00"]}', NULL, N'Dysphagia precautions reduce aspiration risk.', 1),
    (9, 1, 2, N'Escalate swallowing warning signs', N'Stop intake for coughing, choking, wet voice, breathlessness, or reduced alertness; keep resident safe and notify nurse immediately.', 'NURSING', 'NURSE', 'PRN', NULL, N'Any aspiration warning sign or acute change in swallowing.', N'Prompt response is required when aspiration is suspected.', 0),
    (9, 2, 1, N'Assist with oral care', N'Provide setup and cueing or hands-on help to brush teeth, gums, and tongue; clean dentures and moisturise lips.', 'NURSING', 'CNA', 'BID', N'{"times":["08:30","20:00"]}', NULL, N'Twice-daily oral care supports comfort, intake, and pneumonia prevention.', 1),
    (9, 2, 2, N'Complete weekly mouth check', N'Inspect lips, mucosa, tongue, gums, teeth, and dentures; report pain, ulcers, thrush, bleeding, or poor fit.', 'NURSING', 'NURSE', 'WEEKLY', N'{"days":["WED"],"times":["10:00"]}', N'Earlier if oral pain, refusal to eat, or visible lesion occurs.', N'Regular assessment identifies treatable oral problems early.', 1),
    (10, 1, 1, N'Provide bedtime routine', N'Offer preferred warm drink if allowed, support toileting and comfort, dim lights, reduce noise, and play quiet music if requested.', 'NURSING', 'CNA', 'NIGHTLY', N'{"times":["21:00"]}', NULL, N'A consistent person-centred routine supports sleep onset.', 1),
    (10, 1, 2, N'Record sleep pattern', N'Document sleep onset, awakenings, comfort needs, and estimated total sleep without waking the resident solely to observe.', 'NURSING', 'CNA', 'NIGHTLY', N'{"start":"22:00","end":"06:00"}', NULL, N'Sleep-pattern data guides review of reversible causes and interventions.', 1),
    (10, 2, 1, N'Offer preferred meaningful activity', N'Offer a choice of gardening, familiar music, or a small-group activity and adapt duration to energy and interest.', 'ACTIVITIES', 'ACTIVITY_STAFF', 'DAILY', N'{"times":["14:30"]}', NULL, N'Individualised meaningful activity can improve engagement and wellbeing.', 1),
    (10, 2, 2, N'Provide one-to-one social contact', N'If group activity is declined, offer a brief one-to-one conversation or preferred activity without pressure.', 'ACTIVITIES', 'ACTIVITY_STAFF', 'PRN', NULL, N'When resident remains withdrawn or declines the planned group.', N'Choice and low-demand contact respect preference while reducing isolation.', 1);
    DECLARE @PlanNo AS INT = 1, @GoalNo AS INT, @InterventionNo AS INT, @DomainNo AS INT, @ResidentId AS BIGINT, @AdmissionId AS BIGINT, @CarePlanId AS BIGINT, @VersionId AS BIGINT, @GoalId AS BIGINT, @OpenedAt AS DATETIMEOFFSET (7), @EffectiveFrom AS DATETIMEOFFSET (7), @TargetDate AS DATE, @PlanState AS VARCHAR (20), @ClosedAt AS DATETIMEOFFSET (7), @ClosedReason AS VARCHAR (500), @GoalStatus AS VARCHAR (20), @GoalPriority AS VARCHAR (20), @ProblemOrNeed AS NVARCHAR (1000), @GoalTitle AS NVARCHAR (255), @DesiredOutcome AS NVARCHAR (1000), @BaselineValue AS NVARCHAR (2000), @TargetValue AS NVARCHAR (255), @TargetUnit AS VARCHAR (50), @MeasurementMethod AS NVARCHAR (500), @ResidentPreference AS NVARCHAR (1000), @InterventionTitle AS NVARCHAR (255), @Instructions AS NVARCHAR (2000), @ResponsibleDiscipline AS VARCHAR (50), @AssignedRole AS VARCHAR (50), @FrequencyCode AS VARCHAR (30), @ScheduleDefinition AS NVARCHAR (1000), @PrnCriteria AS NVARCHAR (1000), @EvidenceOrRationale AS NVARCHAR (1000), @RequiresTaskGeneration AS BIT;
    WHILE @PlanNo <= 40
        BEGIN
            SET @DomainNo = ((@PlanNo - 1) % 10) + 1;
            SET @ResidentId = @ResidentIdStart + @PlanNo - 1;
            SET @AdmissionId = @AdmissionIdStart + @PlanNo - 1;
            SET @OpenedAt = DATEADD(DAY, -1 * (30 + (@PlanNo * 5)), @SeedAnchor);
            SET @PlanState = CASE WHEN @PlanNo IN (10, 20) THEN 'CLOSED_DISCHARGED' WHEN @PlanNo = 30 THEN 'CLOSED_TRANSFERRED' WHEN @PlanNo = 40 THEN 'CLOSED_DECEASED' WHEN @PlanNo IN (9, 19, 29, 39) THEN 'VOIDED' ELSE 'OPEN' END;
            SET @ClosedAt = CASE WHEN @PlanState = 'OPEN' THEN NULL ELSE DATEADD(DAY, 60, @OpenedAt) END;
            SET @ClosedReason = CASE @PlanState WHEN 'CLOSED_DISCHARGED' THEN 'Resident discharged to the community with follow-up services.' WHEN 'CLOSED_TRANSFERRED' THEN 'Resident transferred to another care facility.' WHEN 'CLOSED_DECEASED' THEN 'Care plan closed following resident death.' WHEN 'VOIDED' THEN 'Administrative duplicate identified during record reconciliation.' ELSE NULL END;
            INSERT  INTO dbo.care_plans (resident_id, admission_id, current_version_id, state, opened_at, closed_at, closed_reason, created_at, created_by, updated_at, updated_by)
            VALUES                     (@ResidentId, @AdmissionId, NULL, @PlanState, @OpenedAt, @ClosedAt, @ClosedReason, @OpenedAt, @ActorId, COALESCE (@ClosedAt, @SeedAnchor), @ActorId);
            SET @CarePlanId = CONVERT (BIGINT, SCOPE_IDENTITY());
            SET @EffectiveFrom = DATEADD(DAY, 2, @OpenedAt);
            INSERT  INTO dbo.care_plan_versions (care_plan_id, version_number, based_on_version_id, based_on_admission_id, created_from_review_id, change_summary, submitted_at, submitted_by, approved_at, approved_by, rejected_at, rejected_by, rejection_reason, effective_from, effective_to, content_hash, created_at, created_by, updated_at, updated_by)
            VALUES                             (@CarePlanId, 1, NULL, @AdmissionId, NULL, 'Initial interdisciplinary care plan based on admission assessment.', DATEADD(DAY, 1, @OpenedAt), @ActorId, @EffectiveFrom, @ActorId, NULL, NULL, NULL, @EffectiveFrom, CASE WHEN @PlanState = 'OPEN' THEN NULL ELSE @ClosedAt END, HASHBYTES('SHA2_256', CONCAT('CARE-PLAN-SEED-', @PlanNo, '-VERSION-1')), @OpenedAt, @ActorId, COALESCE (@ClosedAt, @SeedAnchor), @ActorId);
            SET @VersionId = CONVERT (BIGINT, SCOPE_IDENTITY());
            UPDATE dbo.care_plans
            SET    current_version_id = @VersionId,
                   updated_at         = COALESCE (@ClosedAt, @SeedAnchor),
                   updated_by         = @ActorId
            WHERE  id = @CarePlanId;
            SET @GoalNo = 1;
            WHILE @GoalNo <= 2
                BEGIN
                    SELECT @ProblemOrNeed = problem_or_need,
                           @GoalTitle = title,
                           @DesiredOutcome = desired_outcome,
                           @BaselineValue = baseline_value,
                           @TargetValue = target_value,
                           @TargetUnit = target_unit,
                           @MeasurementMethod = measurement_method,
                           @GoalPriority = priority,
                           @ResidentPreference = resident_preference
                    FROM   @GoalTemplates
                    WHERE  domain_no = @DomainNo
                           AND goal_no = @GoalNo;
                    SET @TargetDate = DATEADD(DAY, CASE WHEN @GoalPriority = 'HIGH' THEN 30 ELSE 45 END, CONVERT (DATE, @EffectiveFrom));
                    SET @GoalStatus = CASE WHEN @PlanState = 'OPEN' THEN 'ACTIVE' WHEN @PlanState = 'VOIDED' THEN 'DISCONTINUED' WHEN @GoalNo = 1 THEN 'ACHIEVED' ELSE 'DISCONTINUED' END;
                    INSERT  INTO dbo.care_goals (care_plan_version_id, problem_or_need, title, desired_outcome, baseline_value, target_value, target_unit, measurement_method, target_date, priority, status, resident_preference, created_at, created_by, updated_at, updated_by)
                    VALUES                     (@VersionId, @ProblemOrNeed, @GoalTitle, @DesiredOutcome, @BaselineValue, @TargetValue, @TargetUnit, @MeasurementMethod, @TargetDate, @GoalPriority, @GoalStatus, @ResidentPreference, @EffectiveFrom, @ActorId, COALESCE (@ClosedAt, @SeedAnchor), @ActorId);
                    SET @GoalId = CONVERT (BIGINT, SCOPE_IDENTITY());
                    SET @InterventionNo = 1;
                    WHILE @InterventionNo <= 2
                        BEGIN
                            SELECT @InterventionTitle = title,
                                   @Instructions = instructions,
                                   @ResponsibleDiscipline = responsible_discipline,
                                   @AssignedRole = assigned_role,
                                   @FrequencyCode = frequency_code,
                                   @ScheduleDefinition = schedule_definition,
                                   @PrnCriteria = prn_criteria,
                                   @EvidenceOrRationale = evidence_or_rationale,
                                   @RequiresTaskGeneration = requires_task_generation
                            FROM   @InterventionTemplates
                            WHERE  domain_no = @DomainNo
                                   AND goal_no = @GoalNo
                                   AND intervention_no = @InterventionNo;
                            INSERT  INTO dbo.care_interventions (care_goal_id, title, instructions, responsible_discipline, assigned_role, frequency_code, schedule_definition, start_date, end_date, prn_criteria, evidence_or_rationale, requires_task_generation, sort_order, created_at, created_by, updated_at, updated_by)
                            VALUES                             (@GoalId, @InterventionTitle, @Instructions, @ResponsibleDiscipline, @AssignedRole, @FrequencyCode, @ScheduleDefinition, CONVERT (DATE, @EffectiveFrom), CASE WHEN @PlanState = 'OPEN' THEN NULL ELSE CONVERT (DATE, @ClosedAt) END, @PrnCriteria, @EvidenceOrRationale, @RequiresTaskGeneration, @InterventionNo * 10, @EffectiveFrom, @ActorId, COALESCE (@ClosedAt, @SeedAnchor), @ActorId);
                            SET @InterventionNo += 1;
                        END
                    SET @GoalNo += 1;
                END
            SET @PlanNo += 1;
        END
    DECLARE @SeededPlanCount AS INT;
    DECLARE @SeededVersionCount AS INT;
    DECLARE @SeededGoalCount AS INT;
    DECLARE @SeededInterventionCount AS INT;
    SELECT @SeededPlanCount = COUNT(*)
    FROM   dbo.care_plans
    WHERE  resident_id BETWEEN @ResidentIdStart AND @ResidentIdStart + 39
           AND admission_id BETWEEN @AdmissionIdStart AND @AdmissionIdStart + 39;
    SELECT @SeededVersionCount = COUNT(*)
    FROM   dbo.care_plan_versions AS v
           INNER JOIN
           dbo.care_plans AS p
           ON p.id = v.care_plan_id
    WHERE  p.resident_id BETWEEN @ResidentIdStart AND @ResidentIdStart + 39
           AND p.admission_id BETWEEN @AdmissionIdStart AND @AdmissionIdStart + 39;
    SELECT @SeededGoalCount = COUNT(*)
    FROM   dbo.care_goals AS g
           INNER JOIN
           dbo.care_plan_versions AS v
           ON v.id = g.care_plan_version_id
           INNER JOIN
           dbo.care_plans AS p
           ON p.id = v.care_plan_id
    WHERE  p.resident_id BETWEEN @ResidentIdStart AND @ResidentIdStart + 39
           AND p.admission_id BETWEEN @AdmissionIdStart AND @AdmissionIdStart + 39;
    SELECT @SeededInterventionCount = COUNT(*)
    FROM   dbo.care_interventions AS i
           INNER JOIN
           dbo.care_goals AS g
           ON g.id = i.care_goal_id
           INNER JOIN
           dbo.care_plan_versions AS v
           ON v.id = g.care_plan_version_id
           INNER JOIN
           dbo.care_plans AS p
           ON p.id = v.care_plan_id
    WHERE  p.resident_id BETWEEN @ResidentIdStart AND @ResidentIdStart + 39
           AND p.admission_id BETWEEN @AdmissionIdStart AND @AdmissionIdStart + 39;
    IF @SeededPlanCount <> 40
       OR @SeededVersionCount <> 40
       OR @SeededGoalCount <> 80
       OR @SeededInterventionCount <> 160
        THROW 50006, 'Seed verification failed; transaction will be rolled back.', 1;
    COMMIT TRANSACTION;
    SELECT @SeededPlanCount AS care_plans_inserted,
           @SeededVersionCount AS care_plan_versions_inserted,
           @SeededGoalCount AS care_goals_inserted,
           @SeededInterventionCount AS care_interventions_inserted;
END TRY
BEGIN CATCH
    IF XACT_STATE() <> 0
        ROLLBACK;
    THROW;
END CATCH



select * 
from care_plan_versions cpv
join care_plans cp on  cpv.care_plan_id = cp.id
where cp.id = 4;

select * from care_plan_versions;


use eldercare;

--List<CarePlan> findAllByAdmissionId(@Param("admisionId") Long admissionId);

update care_plans
set admission_id = 2
where care_plans.id = 2;

select * from care_plans;


select * from admissions;

select *
from care_plans cp
join admissions a on cp.admission_id = a.id
where a.id = 2
;

select * 
from care_plans cp
join care_plan_versions cpv on cpv.care_plan_id = cp.id
where cpv.based_on_admission_id =2
;

select * from care_plan_versions;

select * from users_exam;


exec sp_help care_interventions;