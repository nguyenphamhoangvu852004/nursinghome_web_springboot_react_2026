import { PERMISSIONS, type PermissionCode } from "@/common/permissions";

export interface MockUser {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  roleName: string;
  permissions: PermissionCode[];
}

const nurse: PermissionCode[] = [
  PERMISSIONS.RESIDENT_VIEW,
  PERMISSIONS.RESIDENT_SENSITIVE_VIEW,
  PERMISSIONS.ASSESSMENT_VIEW,
  PERMISSIONS.CARE_PLAN_VIEW,
  PERMISSIONS.CARE_TASK_VIEW,
  PERMISSIONS.NOTIFICATION_VIEW,
];

const cna: PermissionCode[] = [
  PERMISSIONS.RESIDENT_VIEW,
  PERMISSIONS.CARE_TASK_VIEW,
  PERMISSIONS.NOTIFICATION_VIEW,
];

const don: PermissionCode[] = Object.values(PERMISSIONS);

const admissionStaff: PermissionCode[] = [
  PERMISSIONS.RESIDENT_VIEW,
  PERMISSIONS.RESIDENT_SENSITIVE_VIEW,
  PERMISSIONS.SCREENING_VIEW,
  PERMISSIONS.ADMISSION_VIEW,
  PERMISSIONS.FACILITY_VIEW,
  PERMISSIONS.NOTIFICATION_VIEW,
];

export const mockUsersByRole: Record<string, MockUser> = {
  NURSE: {
    id: 1,
    email: "nurse@eldercare.com",
    firstName: "Lan",
    lastName: "Tran",
    roleName: "Nurse",
    permissions: nurse,
  },
  CNA: {
    id: 2,
    email: "cna@eldercare.com",
    firstName: "Minh",
    lastName: "Le",
    roleName: "CNA",
    permissions: cna,
  },
  DON: {
    id: 3,
    email: "don@eldercare.com",
    firstName: "Hoa",
    lastName: "Nguyen",
    roleName: "DON",
    permissions: don,
  },
  ADMISSION_STAFF: {
    id: 4,
    email: "admission@eldercare.com",
    firstName: "Khoa",
    lastName: "Pham",
    roleName: "Admission Staff",
    permissions: admissionStaff,
  },
};

export const CURRENT_MOCK_ROLE: keyof typeof mockUsersByRole = "DON";
