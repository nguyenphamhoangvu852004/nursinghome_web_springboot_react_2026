import {
  LayoutDashboard,
  Users,
  ClipboardCheck,
  ClipboardList,
  Building2,
  Stethoscope,
  ListChecks,
  ScrollText,
  Settings,
  Bell,
  UserCircle,
  ShieldAlert,
  AlarmClock,
  type LucideIcon,
  Database,
  Shield,
  Clock,
  Clock3,
  Package,
} from "lucide-react";
import { PERMISSIONS, type PermissionCode } from "@/common/permissions";

export interface AdminNavItem {
  label: string;
  path: string;
  icon: LucideIcon;
  permission: PermissionCode | PermissionCode[];
}

export interface AdminNavGroup {
  title: string;
  items: AdminNavItem[];
}

export const adminNavGroups: AdminNavGroup[] = [
  {
    title: "Overview",
    items: [
      {
        label: "Dashboard",
        path: "/admin",
        icon: LayoutDashboard,
        permission: PERMISSIONS.RESIDENT_VIEW,
      },
    ],
  },
  {
    title: "User & Role Management",
    items: [
      {
        label: "Users",
        path: "/admin/users",
        icon: Users,
        permission: PERMISSIONS.USER_VIEW,
      },
      {
        label: "Roles",
        path: "/admin/roles",
        icon: Shield,
        permission: [PERMISSIONS.ROLE_VIEW, PERMISSIONS.PERMISSION_VIEW],
      },
    ],
  },
  {
    title: "Resident Intake",
    items: [
      {
        label: "Residents",
        path: "/admin/residents",
        icon: Users,
        permission: PERMISSIONS.RESIDENT_VIEW,
      },
      {
        label: "Pre-Admission",
        path: "/admin/pre-admission",
        icon: ClipboardCheck,
        permission: PERMISSIONS.SCREENING_VIEW,
      },
      {
        label: "Admissions & Bed Assignment",
        path: "/admin/admissions",
        icon: ClipboardList,
        permission: PERMISSIONS.ADMISSION_VIEW,
      },
      {
        label: "Assessment",
        path: "/admin/assessment",
        icon: ClipboardList,
        permission: PERMISSIONS.ASSESSMENT_VIEW,
      },
    ],
  },
  {
    title: "Facility",
    items: [
      {
        label: "Facilities & Rooms/Beds",
        path: "/admin/facilities",
        icon: Building2,
        permission: PERMISSIONS.FACILITY_VIEW,
      },
      {
        label: "LOC Rates",
        path: "/admin/loc-rates",
        icon: ScrollText,
        permission: PERMISSIONS.FACILITY_VIEW,
      },
      {
        label: "Staffing",
        path: "/admin/staffing-ratios",
        icon: Clock3,
        permission: PERMISSIONS.STAFFING_RATIO_VIEW,
      },
      {
        label: "Equipment",
        path: "/admin/equipment",
        icon: Package,
        permission: PERMISSIONS.EQUIPMENT_VIEW,
      },
      {
        label: "Incident Severity",
        path: "/admin/incident-severity",
        icon: ShieldAlert,
        permission: PERMISSIONS.INCIDENT_SEVERITY_VIEW,
      },
      {
        label: "Incidents",
        path: "/admin/incidents",
        icon: ShieldAlert,
        permission: [
          PERMISSIONS.INCIDENT_SEVERITY_VIEW,
          PERMISSIONS.PERMISSION_VIEW,
        ],
      },
      {
        label: "SLA Config",
        path: "/admin/sla-config",
        icon: AlarmClock,
        permission: PERMISSIONS.SLA_CONFIG_VIEW,
      },
    ],
  },
  {
    title: "Care Planning",
    items: [
      {
        label: "Care Plans",
        path: "/admin/care-plans",
        icon: Stethoscope,
        permission: PERMISSIONS.CARE_PLAN_VIEW,
      },
      {
        label: "Care Tasks",
        path: "/admin/care-tasks",
        icon: ListChecks,
        permission: PERMISSIONS.CARE_TASK_VIEW,
      },
    ],
  },
  {
    title: "Compliance",
    items: [
      {
        label: "Audit Logs",
        path: "/admin/audit-logs",
        icon: ScrollText,
        permission: [
          PERMISSIONS.AUDIT_LOG_VIEW,
          PERMISSIONS.PHI_ACCESS_LOG_VIEW,
        ],
      },
    ],
  },
  {
    title: "Data & Seeding",
    items: [
      {
        label: "Demo Data",
        path: "/admin/demo-data",
        icon: Database,
        permission: PERMISSIONS.DEMO_DATA_VIEW,
      },
    ],
  },
  {
    title: "System",
    items: [
      {
        label: "Notifications",
        path: "/admin/notifications",
        icon: Bell,
        permission: PERMISSIONS.NOTIFICATION_VIEW,
      },
      {
        label: "Settings & Permissions",
        path: "/admin/settings",
        icon: Settings,
        permission: [
          PERMISSIONS.ROLE_VIEW,
          PERMISSIONS.PERMISSION_VIEW,
          PERMISSIONS.USER_VIEW,
          PERMISSIONS.CARE_LEVEL_VIEW,
          PERMISSIONS.ASSESSMENT_METRIC_VIEW,
        ],
      },
      {
        label: "Profile",
        path: "/admin/profile",
        icon: UserCircle,
        permission: PERMISSIONS.RESIDENT_VIEW,
      },
    ],
  },
];
