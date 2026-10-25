import type { AssessmentItem } from "../../types/loc.type";

interface Props {
    data: AssessmentItem[];
}

export default function AdlBreakdownTable({ data }: Props) {

    return (
        <div className="mt-8 bg-white rounded-2xl border overflow-hidden">
            <table className="w-full">
                <thead>
                    <tr className="bg-gray-50">
                        <th className="p-4 text-left">
                            Metric
                        </th>
                        <th className="p-4 text-left">
                            Category
                        </th>
                        <th className="p-4 text-left">
                            Score
                        </th>
                        <th className="p-4 text-left">
                            Notes
                        </th>
                    </tr>
                </thead>
                <tbody>
                    {
                        data.map(item => (
                            <tr key={item.metricId} className="border-t">
                                <td className="p-4">
                                    {item.metricName}
                                </td>
                                <td className="p-4">
                                    {item.category}
                                </td>
                                <td className="p-4">
                                    {item.score}
                                </td>
                                <td className="p-4">
                                    {item.notes ?? "-"}
                                </td>
                            </tr>
                        ))
                    }
                </tbody>
            </table>
        </div>
    )
}