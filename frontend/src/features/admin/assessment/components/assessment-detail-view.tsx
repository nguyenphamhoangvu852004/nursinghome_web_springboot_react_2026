import type { AssessmentResponse } from "../types/assessment-type";

export const AssessmentDetailView = ({ row }: { row: AssessmentResponse }) => {
  return (
    <table className="w-full text-sm">
      <thead>
        <tr>
          <th className="text-left">Metric</th>
          <th className="text-left">Category</th>
          <th className="text-left">Score</th>
          <th className="text-left">Notes</th>
        </tr>
      </thead>
      <tbody>
        {row.details.map((d) => (
          <tr key={d.metricId}>
            <td>{d.metricName}</td>
            <td>{d.category}</td>
            <td>{d.score}</td>
            <td>{d.notes}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};
