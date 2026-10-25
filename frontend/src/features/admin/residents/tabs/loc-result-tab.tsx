import {
    useEffect,
    useState
} from "react";

import { residentLocService } from "../services/resident-loc.service";

import type {
    CareLevelHistory,
    LocClassificationResult
} from "../types/loc.type";

import LocSummaryCard from "../components/loc/loc-summary-card";
import AdlBreakdownTable from "../components/loc/adl-breakdown-table";
import LocLevelReference from "../components/loc/loc-level-reference";
import LocRateCard from "../components/loc/loc-rate-card";

interface Props {
    residentId: number;
}

export default function LocResultTab({
    residentId
}: Props) {

    const [history, setHistory] = useState<CareLevelHistory[]>([]);
    const [result, setResult] = useState<LocClassificationResult | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadData();
    }, [residentId]);

    const loadData = async () => {
        try {
            const [historyRes, resultRes] = await Promise.all([
                residentLocService.getHistory(residentId),
                residentLocService.getClassificationResult(residentId)
            ]);

            setHistory(historyRes);
            setResult(resultRes);

        } finally {
            setLoading(false);
        }
    };

    if (loading) return <div>Loading...</div>;

    const current = history.find(x => x.endDate === null);

    return (
        <div className="p-8">

            <LocSummaryCard result={result} />

            <AdlBreakdownTable
                data={result?.details ?? []}
            />

            <LocLevelReference />

            <LocRateCard
                levelName={result?.confirmedCareLevelName}
            />
        </div>
    );
}