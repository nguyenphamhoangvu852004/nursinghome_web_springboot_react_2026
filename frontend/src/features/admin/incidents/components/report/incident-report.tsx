"use client";

import { useState } from "react";

import {
    IncidentDetailsSection,
    type IncidentFormValues,
} from "./incident-details-section";

import { ReportFooter } from "./report-footer";

import { incidentsApi } from "@/services/incidents/incidents-api";

export function IncidentReport() {

    const [form, setForm] = useState<IncidentFormValues>({
        residentLabel: "",
        incidentType: "FALL",
        severityId: undefined,
        residentId: undefined,
        dateTime: "",
        location: "",
        description: "",
        witnesses: "",
    });

    // Không dùng alert nữa. Nếu có lỗi (validate hoặc gọi API thất bại),
    // ném Error ra để ReportFooter bắt và hiển thị bằng dialog.
    const handleSubmit = async () => {
        if (!form.residentId) {
            throw new Error("Vui lòng chọn Resident trước khi báo cáo.");
        }

        if (!form.severityId) {
            throw new Error("Vui lòng chọn mức độ nghiêm trọng (Severity).");
        }

        await incidentsApi.create({

            residentID: form.residentId,

            incidentType: form.incidentType as any,

            severityID: form.severityId,
            occurredAt: form.dateTime.replace("T", " ") + ":00",

            location: form.location,

            description: form.description,

            witnesses: form.witnesses,

        });

    };

    return (

        <>
            <IncidentDetailsSection
                onChange={setForm}
            />

            <ReportFooter
                onSubmit={handleSubmit}
            />
        </>

    );

}