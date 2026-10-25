import apiClient from "@/common/config/api";
import type { SeedDemoDataResponse } from "../types/demo-data.type";

export const seedDemoData = async (
    fixtureFile: File
): Promise<SeedDemoDataResponse> => {
    const formData = new FormData();
    formData.append("fixture_file", fixtureFile);

    const response = await apiClient.post<SeedDemoDataResponse>(
        "/admin/demo-data-seeder",
        formData,
        {
            headers: {
                "Content-Type": "multipart/form-data",
            },
        }
    );

    return response.data;
};

export const exportDemoData = async (
    type: "ResidentList" | "IncidentLog",
    format: "csv" | "pdf"
): Promise<void> => {
    const response = await apiClient.get("/admin/export", {
        params: {
            type,
            format,
        },
        responseType: "blob",
    });

    const blob = new Blob([response.data], {
        type: format === "pdf" ? "application/pdf" : "text/csv;charset=utf-8",
    });

    const url = window.URL.createObjectURL(blob);
    const link = document.createElement("a");

    link.href = url;
    link.download = `${type}.${format}`;
    link.click();

    window.URL.revokeObjectURL(url);
};