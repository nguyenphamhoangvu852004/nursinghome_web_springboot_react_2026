export interface DemoDataset {
    name: string;
    records: string;
    lastSeeded: string;
    status: string;
}

export interface SeedDemoDataResponse {
    seed_job_id: number;
    status: string;
    residents_loaded: number;
    medication_orders_loaded: number;
    incidents_loaded: number;
}
