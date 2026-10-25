import { ENV } from "./env";

export const API_PREFIX = "/api/v1";
export const CARE_PLAN_API_PREFIX = "/api/v2";
// Accept an origin or an existing API base URL without duplicating its prefix.
const serverUrl = ENV.API_URL.replace(/\/+$/, "").replace(/\/api(?:\/v[12])?$/, "");
export const API_BASE_URL = `${serverUrl}${API_PREFIX}`;
export const CARE_PLAN_API_BASE_URL = `${serverUrl}${CARE_PLAN_API_PREFIX}`;
