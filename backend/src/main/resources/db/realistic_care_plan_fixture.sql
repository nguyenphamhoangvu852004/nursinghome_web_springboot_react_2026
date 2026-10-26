-- Fictional demonstration data only. Run manually against the local development database.
-- Replaces the six care-planning tables; preserves admissions and resident demographics.
-- Default is a rollback-only rehearsal. Set @Apply = 1 after exporting a backup.
SET NOCOUNT ON;
SET XACT_ABORT ON;
DECLARE @Apply bit = 0;
DECLARE @Now datetimeoffset(7) = SYSDATETIMEOFFSET() AT TIME ZONE 'SE Asia Standard Time';
DECLARE @Today date = CONVERT(date,@Now);
BEGIN TRY
BEGIN TRANSACTION;
IF DB_NAME() <> 'eldercare' THROW 51000, 'This fixture is restricted to the eldercare development database.', 1;
IF NOT EXISTS (SELECT 1 FROM admissions a JOIN residents r ON r.id=a.resident_id
               WHERE a.is_current=1 AND a.discharge_date IS NULL AND r.is_deleted=0 AND r.status='ACTIVE'
                 AND a.admission_date<=DATEADD(day,-7,@Today))
 THROW 51001, 'No eligible current admissions; refusing to delete care data.', 1;
DECLARE @Author bigint=(SELECT MIN(u.id) FROM users u JOIN roles r ON r.id=u.role_id
 WHERE u.is_deleted=0 AND u.status='ACTIVE' AND r.role_name='NHA_Admin');
IF @Author IS NULL THROW 51002, 'An active administrator is required.', 1;
DECLARE @Cnas TABLE (n int PRIMARY KEY, id bigint);
INSERT @Cnas SELECT ROW_NUMBER() OVER(ORDER BY u.id),u.id FROM users u JOIN roles r ON r.id=u.role_id
 WHERE u.is_deleted=0 AND u.status='ACTIVE' AND r.role_name='CNA';
DECLARE @CnaCount int=(SELECT COUNT(*) FROM @Cnas);
IF @CnaCount=0 THROW 51003, 'At least one active CNA is required.', 1;
-- Capture the actual admission/resident pairs before touching care data.
DECLARE @Residents TABLE(n int PRIMARY KEY,resident_id bigint,admission_id bigint,admitted date);
INSERT @Residents SELECT ROW_NUMBER() OVER(ORDER BY a.id),a.resident_id,a.id,a.admission_date
 FROM admissions a JOIN residents r ON r.id=a.resident_id
 WHERE a.is_current=1 AND a.discharge_date IS NULL AND r.is_deleted=0 AND r.status='ACTIVE'
 AND a.admission_date<=DATEADD(day,-7,@Today)
 AND NOT EXISTS(SELECT 1 FROM admissions newer WHERE newer.resident_id=a.resident_id
   AND newer.is_current=1 AND newer.discharge_date IS NULL AND newer.id>a.id);
DECLARE @Names TABLE(n int PRIMARY KEY,first_name varchar(100),last_name varchar(100));
INSERT @Names VALUES
(1,'Eleanor','Bennett'),
(2,'Arthur','Wilson'),
(3,'Margaret','Thompson'),
(4,'Robert','Harris'),
(5,'Dorothy','Mitchell'),
(6,'William','Parker'),
(7,'Helen','Anderson'),
(8,'George','Edwards'),
(9,'Ruth','Collins'),
(10,'Henry','Turner'),
(11,'Alice','Walker'),
(12,'Charles','Campbell'),
(13,'Rose','Evans'),
(14,'Edward','Phillips'),
(15,'Grace','Roberts'),
(16,'Frank','Stewart'),
(17,'Joan','Morris'),
(18,'Thomas','Morgan'),
(19,'Betty','Reed'),
(20,'Walter','Cooper'),
(21,'Mary','Hughes'),
(22,'Joseph','Brooks'),
(23,'Patricia','Richardson'),
(24,'Albert','Ward'),
(25,'Elizabeth','Foster'),
(26,'Samuel','Sanders'),
(27,'Barbara','Peterson'),
(28,'Richard','Wood'),
(29,'Nancy','Hayes'),
(30,'Donald','Russell'),
(31,'Susan','Griffin'),
(32,'Paul','Price'),
(33,'Carol','Henderson'),
(34,'Kenneth','Coleman'),
(35,'Linda','Jenkins'),
(36,'Harold','Sullivan');
-- Only remove explicit placeholder names on participating resident profiles.
UPDATE r SET first_name=nm.first_name,last_name=nm.last_name,middle_name=NULL,updated_at=@Now
 FROM residents r JOIN @Residents selected ON selected.resident_id=r.id JOIN @Names nm ON nm.n=selected.n
 WHERE r.last_name IN ('VisualDemo','Demo') AND (r.first_name LIKE 'Resident%' OR r.first_name='Eleanor');
-- Break optional circular references, then delete children before parents.
DELETE FROM care_tasks;
UPDATE care_plans SET current_version_id=NULL;
UPDATE care_plan_versions SET based_on_version_id=NULL,created_from_review_id=NULL;
DELETE FROM care_plan_reviews;
DELETE FROM care_interventions;
DELETE FROM care_goals;
DELETE FROM care_plan_versions;
DELETE FROM care_plans;
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
    (2, 2, 2, N'Position limbs in neutral alignment', N'Use prescribed supports and check that joints are aligned without pressure or pain.', 'NURSING', 'CNA', 'Q4H', N'{"intervalHours":4,"times":["00:00","04:00","08:00","12:00","16:00","20:00"]}', N'After repositioning, return from activity, or reported discomfort.', N'Consistent positioning reduces contracture and pressure risk.', 1),
    (3, 1, 1, N'Provide fortified preferred meals', N'Offer dietitian-approved fortified foods in small portions, assist as needed, and record percentage consumed.', 'DIETETICS', 'CNA', 'TID_MEALS', N'{"times":["08:00","12:00","18:00"]}', NULL, N'Energy-dense preferred food supports intake and weight stability.', 1),
    (3, 1, 2, N'Monitor weekly weight', N'Weigh under consistent conditions and notify nurse or dietitian of loss greater than 1 kg or ongoing intake below target.', 'NURSING', 'NURSE', 'WEEKLY', N'{"days":["MON"],"times":["07:30"]}', N'Earlier if oedema, acute illness, or visible nutritional decline occurs.', N'Trending weight and intake identifies nutritional deterioration early.', 1),
    (3, 2, 1, N'Offer preferred fluids', N'Offer prescribed fluids in the preferred cup, prompt gently, and record intake unless a restriction applies.', 'NURSING', 'CNA', 'Q2H', N'{"intervalHours":2,"start":"08:00","end":"20:00","times":["08:00","10:00","12:00","14:00","16:00","18:00","20:00"]}', N'After activity, toileting, fever, vomiting, or resident request.', N'Frequent supported offers improve hydration in residents with low thirst drive.', 1),
    (3, 2, 2, N'Review hydration status', N'Review fluid total, urine pattern, oral moisture, and clinical signs; escalate intake below target or dehydration concerns.', 'NURSING', 'NURSE', 'QSHIFT', N'{"times":["06:00","14:00","22:00"]}', NULL, N'Shift review supports early recognition of dehydration.', 1),
    (4, 1, 1, N'Reposition and offload pressure', N'Reposition using the documented turning plan, float heels when in bed, and confirm pressure-relieving equipment is correctly placed.', 'NURSING', 'CNA', 'Q2H', N'{"intervalHours":2,"times":["00:00","02:00","04:00","06:00","08:00","10:00","12:00","14:00","16:00","18:00","20:00","22:00"]}', N'Sooner for discomfort, moisture, or visible pressure redness.', N'Immobility and high Braden risk require scheduled pressure redistribution.', 1),
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
    (10, 1, 2, N'Record sleep pattern', N'Document sleep onset, awakenings, comfort needs, and estimated total sleep without waking the resident solely to observe.', 'NURSING', 'CNA', 'NIGHTLY', N'{"times":["06:00"],"observationWindow":{"start":"22:00","end":"06:00"}}', NULL, N'Sleep-pattern data guides review of reversible causes and interventions.', 1),
    (10, 2, 1, N'Offer preferred meaningful activity', N'Offer a choice of gardening, familiar music, or a small-group activity and adapt duration to energy and interest.', 'ACTIVITIES', 'ACTIVITY_STAFF', 'DAILY', N'{"times":["14:30"]}', NULL, N'Individualised meaningful activity can improve engagement and wellbeing.', 1),
    (10, 2, 2, N'Provide one-to-one social contact', N'If group activity is declined, offer a brief one-to-one conversation or preferred activity without pressure.', 'ACTIVITIES', 'ACTIVITY_STAFF', 'PRN', NULL, N'When resident remains withdrawn or declines the planned group.', N'Choice and low-demand contact respect preference while reducing isolation.', 1);

DECLARE @N int=1,@Count int=(SELECT COUNT(*) FROM @Residents),@Resident bigint,@Admission bigint,
 @Admitted date,@Opened datetimeoffset(7),@Created datetimeoffset(7),@Effective datetimeoffset(7),
 @Plan bigint,@Version bigint,@Previous bigint,@Review bigint,@V int,@VCount int,@Kind int,
 @Domain int,@Extra int,@Status varchar(20),@Goal bigint;
DECLARE @VersionMap TABLE(n int,v int,id bigint,PRIMARY KEY(n,v));
DECLARE @GoalMap TABLE(version_id bigint,domain_no int,goal_no int,goal_id bigint);
WHILE @N<=@Count
BEGIN
 SELECT @Resident=resident_id,@Admission=admission_id,@Admitted=admitted FROM @Residents WHERE n=@N;
 SET @Opened=TODATETIMEOFFSET(DATEADD(hour,9,CAST(@Admitted AS datetime2)), '+07:00');
 SET @Kind=(@N-1)%6;
 SET @Domain=(@N-1)%10+1;
 -- Related secondary needs: falls + continence; mobility + skin; nutrition + swallowing, etc.
 SET @Extra=CASE @Domain WHEN 1 THEN 7 WHEN 2 THEN 4 WHEN 3 THEN 9 WHEN 4 THEN 2
 WHEN 5 THEN 1 WHEN 6 THEN 10 WHEN 7 THEN 4 WHEN 8 THEN 2 WHEN 9 THEN 3 ELSE 6 END;
 INSERT care_plans(resident_id,admission_id,current_version_id,state,opened_at,created_at,created_by,updated_at,updated_by)
 VALUES(@Resident,@Admission,NULL,'OPEN',@Opened,@Opened,@Author,@Now,@Author);
 SET @Plan=SCOPE_IDENTITY(); SET @Previous=NULL; SET @V=1;
 SET @VCount=CASE WHEN @Kind IN (3,4,5) THEN 2 ELSE 1 END;
 WHILE @V<=@VCount
 BEGIN
  SET @Status=CASE WHEN @V=1 AND @Kind=0 THEN 'DRAFT' WHEN @V=1 AND @Kind=1 THEN 'PENDING_APPROVAL'
   WHEN @V=1 AND @Kind=5 THEN 'SUPERSEDED' WHEN @V=2 AND @Kind=3 THEN 'DRAFT'
   WHEN @V=2 AND @Kind=4 THEN 'PENDING_APPROVAL' ELSE 'EFFECTIVE' END;
  SET @Created=CASE WHEN @V=1 THEN @Opened ELSE DATEADD(day,-3,@Now) END;
  SET @Effective=DATEADD(day,2,@Created);
  INSERT care_plan_versions(care_plan_id,version_number,based_on_version_id,based_on_admission_id,status,
   change_summary,submitted_at,submitted_by,approved_at,approved_by,effective_from,effective_to,
   created_at,created_by,updated_at,updated_by)
  VALUES(@Plan,@V,@Previous,@Admission,@Status,
   CASE WHEN @V=1 THEN 'Admission care plan: individual goals, preferences, safety precautions and shift responsibilities.'
    WHEN @Kind=5 THEN 'Mobility improved during supervised care; review assistance level and retain safety precautions.'
    WHEN @Kind=4 THEN 'Proposed update after reduced participation: shorter care sessions with rest breaks and closer observation.'
    ELSE 'Working revision: clarify measurable outcomes and resident preferences before submission.' END,
   CASE WHEN @Status<>'DRAFT' THEN DATEADD(hour,20,@Created) END,
   CASE WHEN @Status<>'DRAFT' THEN @Author END,
   CASE WHEN @Status IN ('EFFECTIVE','SUPERSEDED') THEN @Effective END,
   CASE WHEN @Status IN ('EFFECTIVE','SUPERSEDED') THEN @Author END,
   CASE WHEN @Status IN ('EFFECTIVE','SUPERSEDED') THEN @Effective END,
   CASE WHEN @Status='SUPERSEDED' THEN DATEADD(day,-1,@Now) END,
   @Created,@Author,CASE WHEN @V=2 OR @Status IN ('DRAFT','PENDING_APPROVAL') THEN @Now ELSE @Effective END,@Author);
  SET @Version=SCOPE_IDENTITY();
  INSERT @VersionMap VALUES(@N,@V,@Version);
  IF @Status='EFFECTIVE' UPDATE care_plans SET current_version_id=@Version WHERE id=@Plan;
  INSERT care_goals(care_plan_version_id,problem_or_need,title,desired_outcome,baseline_value,target_value,target_unit,
   measurement_method,target_date,priority,status,resident_preference,created_at,created_by,updated_at,updated_by)
  OUTPUT inserted.care_plan_version_id,inserted.id INTO @GoalMap(version_id,goal_id)
  SELECT @Version,t.problem_or_need,t.title,t.desired_outcome,
   CASE WHEN @V=2 AND @Kind=5 AND t.domain_no IN (1,2) THEN N'Now tolerates one-person assistance with verbal cueing; reassess fatigue at each session.'
   ELSE t.baseline_value END,t.target_value,t.target_unit,t.measurement_method,
   DATEADD(day,CASE WHEN t.priority='HIGH' THEN 30 ELSE 45 END,CONVERT(date,@Created)),
   CASE WHEN t.domain_no=@Extra THEN 'MEDIUM' ELSE t.priority END,
   CASE WHEN @Status='SUPERSEDED' THEN CASE WHEN t.goal_no=1 THEN 'ACHIEVED' ELSE 'DISCONTINUED' END ELSE 'ACTIVE' END,
   t.resident_preference,@Created,@Author,@Created,@Author
  FROM @GoalTemplates t WHERE t.domain_no=@Domain OR (t.domain_no=@Extra AND t.goal_no=1);
  -- Match the newly inserted goals back to the explicit template keys.
  UPDATE m SET domain_no=t.domain_no,goal_no=t.goal_no FROM @GoalMap m JOIN care_goals g ON g.id=m.goal_id
   JOIN @GoalTemplates t ON t.title=g.title WHERE m.version_id=@Version;
  INSERT care_interventions(care_goal_id,title,instructions,responsible_discipline,assigned_role,frequency_code,
   schedule_definition,start_date,end_date,prn_criteria,evidence_or_rationale,requires_task_generation,sort_order,
   created_at,created_by,updated_at,updated_by)
  SELECT m.goal_id,t.title,t.instructions,t.responsible_discipline,t.assigned_role,t.frequency_code,
   t.schedule_definition,CONVERT(date,COALESCE(CASE WHEN @Status IN ('EFFECTIVE','SUPERSEDED') THEN @Effective END,@Created)),
   CASE WHEN @Status='SUPERSEDED' THEN CONVERT(date,DATEADD(day,-1,@Now)) END,
   t.prn_criteria,t.evidence_or_rationale,t.requires_task_generation,t.intervention_no*10,@Created,@Author,@Created,@Author
   FROM @GoalMap m JOIN @InterventionTemplates t ON t.domain_no=m.domain_no AND t.goal_no=m.goal_no
   WHERE m.version_id=@Version;
  IF @V=2
  BEGIN
   INSERT care_plan_reviews(care_plan_id,review_type,status,source_assessment_id,previous_version_id,resulting_version_id,trigger_source,
    triggered_at,determined_at,scheduled_due_at,started_at,completed_at,material_change,decision_notes,created_at,created_by,completed_by)
   VALUES(@Plan,'OTHER_CLINICAL',CASE WHEN @Kind=5 THEN 'COMPLETED' ELSE 'IN_PROGRESS' END,
    (SELECT TOP 1 id FROM assessments WHERE admission_id=@Admission AND resident_id=@Resident AND status='COMPLETED' ORDER BY created_at DESC),
    @Previous,@Version,'NURSING_OBSERVATION',DATEADD(hour,-2,@Created),@Created,DATEADD(day,4,@Created),@Created,
    CASE WHEN @Kind=5 THEN @Effective END,1,
    CASE WHEN @Kind=5 THEN N'Reviewed transfer tolerance and resident feedback. Updated assistance instructions approved; previous version retained for audit.'
    WHEN @Kind=4 THEN N'Draft discussed with resident. Candidate submitted for approval; current effective instructions remain in use.'
    ELSE N'Routine clinical review in progress. Resident preferences and measurable targets being reconciled before submission.' END,
    @Created,@Author,CASE WHEN @Kind=5 THEN @Author END);
   SET @Review=SCOPE_IDENTITY();
   UPDATE care_plan_versions SET created_from_review_id=@Review WHERE id=@Version;
  END;
  SET @Previous=@Version; SET @V+=1;
 END;
 SET @N+=1;
END;
-- Five-day CNA schedule, derived from actual intervention times (including interval schedules).
-- PRN/event-based guidance is deliberately not converted into invented timed tasks.
;WITH Days AS (SELECT d FROM (VALUES(-2),(-1),(0),(1),(2)) x(d)), Slots AS (
 SELECT i.id intervention_id,g.desired_outcome,i.title,i.frequency_code,
  TODATETIMEOFFSET(CAST(CONCAT(CONVERT(varchar(10),DATEADD(day,d.d,@Today),23),'T',j.value,':00') AS datetime2),'+07:00') scheduled,
  ROW_NUMBER() OVER(ORDER BY p.id,i.id,d.d,j.value) seq
 FROM care_plans p JOIN care_plan_versions v ON v.id=p.current_version_id AND v.status='EFFECTIVE'
 JOIN care_goals g ON g.care_plan_version_id=v.id AND g.status='ACTIVE'
 JOIN care_interventions i ON i.care_goal_id=g.id CROSS JOIN Days d
 CROSS APPLY OPENJSON(i.schedule_definition,'$.times') j
 WHERE p.state='OPEN' AND i.assigned_role='CNA' AND i.requires_task_generation=1 AND i.frequency_code<>'PRN'
 AND DATEADD(day,d.d,@Today)>=i.start_date AND (i.end_date IS NULL OR DATEADD(day,d.d,@Today)<=i.end_date)
 AND (JSON_QUERY(i.schedule_definition,'$.days') IS NULL OR EXISTS(
  SELECT 1 FROM OPENJSON(i.schedule_definition,'$.days') wd
  WHERE wd.value=CHOOSE((DATEDIFF(day,'19000101',DATEADD(day,d.d,@Today))%7)+1,'MON','TUE','WED','THU','FRI','SAT','SUN')))
), Outcomes AS (
 SELECT *,CASE WHEN scheduled>DATEADD(minute,-30,@Now) THEN 'PENDING' WHEN seq%13=0 THEN 'MISSED' ELSE 'COMPLETED' END outcome
 FROM Slots WHERE scheduled>= (SELECT effective_from FROM care_plan_versions v JOIN care_goals g ON g.care_plan_version_id=v.id
 JOIN care_interventions i ON i.care_goal_id=g.id WHERE i.id=Slots.intervention_id)
)
INSERT care_tasks(care_intervention_id,assigned_cna_id,task_type,scheduled_time,status,completed_at,is_abnormal_flagged,goal)
SELECT o.intervention_id,CASE WHEN o.outcome='PENDING' AND o.seq%11=0 THEN NULL ELSE c.id END,
 CASE WHEN o.title LIKE '%fluid%' THEN 'HYDRATION' WHEN o.title LIKE '%meal%' OR o.title LIKE '%swallow%' THEN 'MEAL_ASSISTANCE'
 WHEN o.title LIKE '%Reposition%' OR o.title LIKE '%alignment%' THEN 'REPOSITIONING'
 WHEN o.title LIKE '%toilet%' OR o.title LIKE '%continence%' THEN 'TOILETING'
 WHEN o.title LIKE '%oral%' THEN 'ORAL_CARE' WHEN o.title LIKE '%walk%' OR o.title LIKE '%stand%' OR o.title LIKE '%motion%' THEN 'MOBILITY'
 WHEN o.title LIKE '%skin%' OR o.title LIKE '%moistur%' THEN 'SKIN_CARE' WHEN o.title LIKE '%sleep%' OR o.title LIKE '%bedtime%' THEN 'SLEEP_SUPPORT'
 ELSE 'PERSONAL_CARE' END,
 o.scheduled,o.outcome,CASE WHEN o.outcome='COMPLETED' THEN DATEADD(minute,5+o.seq%20,o.scheduled) END,
 CASE WHEN o.outcome='COMPLETED' AND o.seq%29=0 THEN 1 ELSE 0 END,
 CONCAT(o.desired_outcome,CHAR(10),N'Care: ',o.title,CHAR(10),
 CASE WHEN o.outcome='MISSED' THEN N'Not completed: resident declined during scheduled visit; nurse informed and follow-up requested.'
 WHEN o.outcome='PENDING' THEN N'Planned care; confirm resident consent and current instructions before starting.'
 WHEN o.seq%29=0 THEN N'Resident reported fatigue; care paused, safety maintained, nurse notified for reassessment.'
 ELSE N'Care completed with resident consent; tolerated at documented assistance level.' END)
FROM Outcomes o JOIN @Cnas c ON c.n=((o.seq-1)%@CnaCount)+1;
-- All checks execute before commit. Any failed check rolls the entire replacement back.
IF (SELECT COUNT(*) FROM care_plans)<>@Count THROW 51010,'Plan count mismatch.',1;
IF EXISTS(SELECT 1 FROM care_plans p LEFT JOIN admissions a ON a.id=p.admission_id AND a.resident_id=p.resident_id WHERE a.id IS NULL)
 THROW 51011,'Care plan admission/resident mismatch.',1;
IF EXISTS(SELECT 1 FROM care_plans p JOIN care_plan_versions v ON v.id=p.current_version_id WHERE v.status<>'EFFECTIVE' OR v.care_plan_id<>p.id)
 THROW 51012,'Invalid current effective version.',1;
IF EXISTS(SELECT 1 FROM care_tasks t LEFT JOIN care_interventions i ON i.id=t.care_intervention_id
 LEFT JOIN care_goals g ON g.id=i.care_goal_id LEFT JOIN care_plan_versions v ON v.id=g.care_plan_version_id
 LEFT JOIN care_plans p ON p.current_version_id=v.id
 WHERE p.id IS NULL OR v.status<>'EFFECTIVE' OR i.assigned_role<>'CNA' OR g.status<>'ACTIVE'
 OR t.scheduled_time<v.effective_from OR (t.status='COMPLETED' AND (t.completed_at<t.scheduled_time OR t.completed_at>@Now))
 OR (t.status<>'COMPLETED' AND t.completed_at IS NOT NULL)) THROW 51013,'Invalid generated task timeline or version.',1;
IF EXISTS(SELECT 1 FROM care_tasks t JOIN users u ON u.id=t.assigned_cna_id JOIN roles r ON r.id=u.role_id WHERE r.role_name<>'CNA')
 THROW 51014,'Task assigned to non-CNA.',1;
SELECT @Apply applied,@Now fixture_time,(SELECT COUNT(*) FROM care_plans) plans,
 (SELECT COUNT(*) FROM care_plan_versions) versions,(SELECT COUNT(*) FROM care_goals) goals,
 (SELECT COUNT(*) FROM care_interventions) interventions,(SELECT COUNT(*) FROM care_plan_reviews) reviews,
 (SELECT COUNT(*) FROM care_tasks) tasks;
SELECT status,COUNT(*) total FROM care_plan_versions GROUP BY status;
SELECT status,COUNT(*) total FROM care_tasks GROUP BY status;
IF @Apply=1 COMMIT TRANSACTION; ELSE ROLLBACK TRANSACTION;
END TRY
BEGIN CATCH
 IF XACT_STATE()<>0 ROLLBACK TRANSACTION;
 THROW;
END CATCH;
