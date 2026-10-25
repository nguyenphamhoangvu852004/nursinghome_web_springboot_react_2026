interface Props {
    levelName?: string;
}

export default function LocRateCard({ levelName }: Props) {
    return (
        <div className="mt-8 bg-white border rounded-2xl p-6">
            <p className="text-gray-500 text-sm">
                Estimated Daily Rate
            </p>

            <h2 className="text-3xl font-bold mt-2">
                $248/day
            </h2>

            <p className="text-sm text-gray-500 mt-2">
                {levelName ?? "-"}
            </p>

            <p className="text-xs text-gray-400 mt-1">
                From LOC Rate Table
            </p>
        </div>
    );
}