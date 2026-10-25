export const TIERS = [
  { id: 1, name: 'Tier 1 — Low', range: '0 - 8', defaultRate: '165.00' },
  { id: 2, name: 'Tier 2 — Moderate', range: '9 - 16', defaultRate: '205.00' },
  { id: 3, name: 'Tier 3 — High', range: '17 - 24', defaultRate: '248.00' },
  { id: 4, name: 'Tier 4 — Total', range: '25 - 32', defaultRate: '310.00' },
];

export const CURRENT_USER = {
  name: 'Victor Alvarez',
  role: 'System Admin',
  initials: 'VA',
};

// Sửa từ process.env thành import.meta.env cho Vite
export { API_BASE_URL } from "@/common/config/api-url";
