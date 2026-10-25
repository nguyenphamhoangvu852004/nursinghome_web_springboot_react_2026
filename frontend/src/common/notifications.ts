export interface NotificationItem {
  id: string;
  title: string;
  type: string;
  isRead: boolean;
  createdAt: string;
}

export const mockNotifications: NotificationItem[] = [
  {
    id: "1",
    title: "New invoice #INV-2044 has been issued",
    type: "BILLING",
    isRead: false,
    createdAt: "2h ago",
  },
  {
    id: "2",
    title: "Care plan updated for resident",
    type: "CARE_PLAN",
    isRead: false,
    createdAt: "5h ago",
  },
  {
    id: "3",
    title: "Medication schedule changed",
    type: "MEDICATION",
    isRead: true,
    createdAt: "1d ago",
  },
];
