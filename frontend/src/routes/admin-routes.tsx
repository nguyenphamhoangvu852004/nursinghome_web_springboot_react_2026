import AdmissionPage from "@/features/admin/admissions/pages/admission-page";
import PreAdmissionPage from "@/features/admin/pre-admission/pages/pre-admission-page";
import AuditlogPage from "@/features/admin/audit-logs/pages/audit-log-page";
import CarePlanPage from "@/features/admin/care-plans/pages/care-plan-page";
import CreateCarePlanPage from "@/features/admin/care-plans/pages/create-care-plan-page";
import CareTaskPage from "@/features/admin/care-tasks/pages/care-task-page";
import DashboardPage from "@/features/admin/dashboard/pages/dashboard-page";
import FacilityPage from "@/features/admin/facilities/pages/facility-page";
import IncidentSeverityPage from "@/features/admin/incident-severity/pages/incident-severity-page";
import IncidentPage from "@/features/admin/incidents/pages/incident-page";
import NotificationPage from "@/features/admin/notifications/pages/notification-page";
import ProfilePage from "@/features/admin/profile/pages/profile-page";
import ResidentPage from "@/features/admin/residents/pages/resident-page";
import ResidentDetailPage from "@/features/admin/residents/pages/resident-detail-page";
import ResidentEditPage from "@/features/admin/residents/pages/resident-edit-page";
import SlaConfigPage from "@/features/admin/sla-config/pages/sla-config-page";
import SettingPage from "@/features/admin/settings/pages/setting-page";
import { AdminLayout } from "@/layouts/admin-layout";
import { PERMISSIONS } from "@/common/permissions";
import type { RouteObject } from "react-router";
import { RequirePermission } from "@/components/common/require-permission";
import { FacilityDetailPage } from "@/features/admin/facilities/pages/facility-detail-page";
import DemoDataPage from "@/features/admin/demo-data/pages/demo-data-page";
import StaffingRatioPage from "@/features/admin/staffing-ratios/pages/staffing-ratio-page";
import RolePage from "@/features/admin/roles/pages/role-page";
import { AssessmentPage } from "@/features/admin/assessment/pages/assessment-page";
import CarePlanReviewPage from "@/features/admin/care-plans/pages/care-plan-review-page";
import CarePlanDetailPage from "@/features/admin/care-plans/pages/care-plan-detail/care-plan-detail-page";
import EquipmentPage from "@/features/admin/inventory/pages/equipment-page";
import CarePlanEditorPage from "@/features/admin/care-plans/pages/care-plan-editor-page";

import { LOCRatesPage } from "@/features/admin/facilities/pages/loc-rates-page";

export const adminRoutes: RouteObject = {
  path: "/admin",
  element: <AdminLayout />,
  children: [
    { index: true, element: <DashboardPage /> },
    {
      path: "roles",
      element: (
        <RequirePermission
          permission={[PERMISSIONS.ROLE_VIEW, PERMISSIONS.PERMISSION_VIEW]}
        >
          <RolePage />
        </RequirePermission>
      ),
    },
    {
      path: "residents",
      element: (
        <RequirePermission permission={PERMISSIONS.RESIDENT_VIEW}>
          <ResidentPage />
        </RequirePermission>
      ),
    },
    {
      path: "residents/:id",
      element: (
        <RequirePermission permission={PERMISSIONS.RESIDENT_VIEW}>
          <ResidentDetailPage />
        </RequirePermission>
      ),
    },
    {
      path: "residents/:id/edit",
      element: (
        <RequirePermission permission={PERMISSIONS.RESIDENT_VIEW}>
          <ResidentEditPage />
        </RequirePermission>
      ),
    },
    {
      path: "pre-admission",
      element: (
        <RequirePermission permission={PERMISSIONS.SCREENING_VIEW}>
          <PreAdmissionPage />
        </RequirePermission>
      ),
    },
    {
      path: "assessment",
      element: (
        <RequirePermission permission={PERMISSIONS.ASSESSMENT_VIEW}>
          <AssessmentPage />
        </RequirePermission>
      ),
    },
    {
      path: "admissions",
      element: (
        <RequirePermission permission={PERMISSIONS.ADMISSION_VIEW}>
          <AdmissionPage />
        </RequirePermission>
      ),
    },
    {
      path: "facilities",
      element: (
        <RequirePermission permission={PERMISSIONS.FACILITY_VIEW}>
          <FacilityPage />
        </RequirePermission>
      ),
    },
    {
      path: "facilities/:id",
      element: (
        <RequirePermission permission={PERMISSIONS.FACILITY_VIEW}>
          <FacilityDetailPage />
        </RequirePermission>
      ),
    },
    {
      path: "loc-rates",
      element: (
        <RequirePermission permission={PERMISSIONS.FACILITY_VIEW}>
          <LOCRatesPage />
        </RequirePermission>
      ),
    },
    {
      path: "staffing-ratios",
      element: (
        <RequirePermission permission={PERMISSIONS.FACILITY_VIEW}>
          <StaffingRatioPage />
        </RequirePermission>
      ),
    },
    {
      path: "incident-severity",
      element: (
        <RequirePermission permission={PERMISSIONS.INCIDENT_SEVERITY_VIEW}>
          <IncidentSeverityPage />
        </RequirePermission>
      ),
    },
    {
      path: "incidents",
      element: (
        <RequirePermission permission={PERMISSIONS.INCIDENTS_VIEW}>
          <IncidentPage />
        </RequirePermission>
      ),
    },

    {
      path: "sla-config",
      element: (
        <RequirePermission permission={PERMISSIONS.SLA_CONFIG_VIEW}>
          <SlaConfigPage />
        </RequirePermission>
      ),
    },
    {
      path: "care-plans",
      element: (
        <RequirePermission permission={PERMISSIONS.CARE_PLAN_VIEW}>
          <CarePlanPage />
        </RequirePermission>
      ),
    },
    {
      path: "care-plans/new",
      element: (
        <RequirePermission permission={PERMISSIONS.CARE_PLAN_VIEW}>
          <CreateCarePlanPage />
        </RequirePermission>
      ),
    },
    {
      path: "care-plans/:id/edit",
      element: <RequirePermission permission={PERMISSIONS.CARE_PLAN_VIEW}><CarePlanEditorPage /></RequirePermission>,
    },
    {
      path: "care-plans/:id",
      element: (
        <RequirePermission permission={PERMISSIONS.CARE_PLAN_VIEW}>
          <CarePlanDetailPage />
        </RequirePermission>
      ),
    },
    {
      path: "care-plans/review/:id",
      element: (
        <RequirePermission permission={PERMISSIONS.CARE_PLAN_VIEW}>
          <CarePlanReviewPage />
        </RequirePermission>
      ),
    },
    {
      path: "care-tasks",
      element: (
        <RequirePermission permission={PERMISSIONS.CARE_TASK_VIEW}>
          <CareTaskPage />
        </RequirePermission>
      ),
    },
    {
      path: "audit-logs",
      element: (
        <RequirePermission
          permission={[
            PERMISSIONS.AUDIT_LOG_VIEW,
            PERMISSIONS.PHI_ACCESS_LOG_VIEW,
          ]}
        >
          <AuditlogPage />
        </RequirePermission>
      ),
    },
    {
      path: "notifications",
      element: (
        <RequirePermission permission={PERMISSIONS.NOTIFICATION_VIEW}>
          <NotificationPage />
        </RequirePermission>
      ),
    },
    {
      path: "demo-data",
      element: (
        <RequirePermission permission={PERMISSIONS.USER_VIEW}>
          <DemoDataPage />
        </RequirePermission>
      ),
    },
    {
      path: "equipment",
      element: (
        <RequirePermission permission={PERMISSIONS.EQUIPMENT_VIEW}>
          <EquipmentPage />
        </RequirePermission>
      ),
    },
    {
      path: "settings",
      element: (
        <RequirePermission
          permission={[
            PERMISSIONS.ROLE_VIEW,
            PERMISSIONS.PERMISSION_VIEW,
            PERMISSIONS.USER_VIEW,
            PERMISSIONS.CARE_LEVEL_VIEW,
            PERMISSIONS.ASSESSMENT_METRIC_VIEW,
          ]}
        >
          <SettingPage />
        </RequirePermission>
      ),
    },
    { path: "profile", element: <ProfilePage /> },
  ],
};
