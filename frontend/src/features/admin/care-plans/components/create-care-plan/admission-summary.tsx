import type { AdmissionResponse } from "@/features/admin/admissions/types/admission-type";

export function AdmissionSummary({ admission }: { admission: AdmissionResponse }) {
  return (
    <dl className="grid gap-5 text-sm sm:grid-cols-2">
      {[
        ["Resident", admission.residentName],
        ["Resident ID", `#${admission.residentId}`],
        ["Admission", `#${admission.id}`],
        ["Admission date", admission.admissionDate.slice(0, 10)],
        ["Facility", `#${admission.facilityId}`],
        ["Admission status", "Active"],
      ].map(([label, value]) => (
        <div key={label}>
          <dt className="text-muted-foreground">{label}</dt>
          <dd className="mt-1 font-medium break-words">{value}</dd>
        </div>
      ))}
    </dl>
  );
}
