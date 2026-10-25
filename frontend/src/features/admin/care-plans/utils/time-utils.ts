export function formatOffsetDateTimeToDate(date?: string | null) {
  if (!date) return "—";

  return date.split("T")[0];
}

export function formatOffsetDateTimeToDateTime(date?: string | null): string {
  if (!date) return "—";

  return new Intl.DateTimeFormat("en-GB", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
    hour12: false,
  }).format(new Date(date));
}
