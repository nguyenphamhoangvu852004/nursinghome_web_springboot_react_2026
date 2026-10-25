export function versionLabel(id: number | null, number: number | null) {
  if (id == null) return "No version";
  return number != null && number > 0 ? `Version ${number}` : `Version #${id}`;
}

export function formatCarePlanDate(value: string | null, withTime = false) {
  if (!value) return "—";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "—";
  return new Intl.DateTimeFormat(undefined, {
    dateStyle: "medium", ...(withTime ? { timeStyle: "short" as const } : {}),
  }).format(date);
}
