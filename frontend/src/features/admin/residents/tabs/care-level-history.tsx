import { useEffect, useState } from "react";
import { useParams } from "react-router";
import { axiosInstance } from "@/lib/axios";

interface CareLevelHistory {
  id: number;
  careLevelId: number;
  levelCode: string;
  action: string;
  startDate: string;
  endDate: string | null;
}

export const CareLevelHistoryTab = () => {
  const { id } = useParams();

  console.log("Resident ID:", id);

  const [histories, setHistories] = useState<CareLevelHistory[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!id) {
      console.log("Resident ID is undefined");
      setLoading(false);
      return;
    }

    const fetchHistory = async () => {
      try {
        console.log("Calling API...");

        const response = await axiosInstance.get(
          `/residents/${id}/care-level-history`
        );

        console.log("API Response:", response.data);

        setHistories(response.data);
      } catch (error) {
        console.error("Failed to load care level history", error);
      } finally {
        setLoading(false);
      }
    };

    fetchHistory();
  }, [id]);

  if (loading) {
    return <div>Loading care level history...</div>;
  }

  return (
    <div className="bg-white rounded-2xl border p-6">
      <h2 className="text-lg font-semibold mb-4">
        Care Level History
      </h2>

      {histories.length === 0 ? (
        <p className="text-gray-500">
          No care level history found.
        </p>
      ) : (
        <table className="w-full">
          <thead>
            <tr className="border-b">
              <th className="text-left py-3">Action</th>
              <th className="text-left py-3">Level</th>
              <th className="text-left py-3">Start Date</th>
              <th className="text-left py-3">End Date</th>
            </tr>
          </thead>

          <tbody>
            {histories.map((item) => {
              console.log("History Item:", item);

              return (
                <tr key={item.id} className="border-b">
                  <td className="py-3">
                    <span
                      className={`inline-flex items-center rounded-full px-2.5 py-1 text-xs font-medium ${
                        item.action === "CONFIRMED"
                          ? "bg-green-100 text-green-700"
                          : "bg-yellow-100 text-yellow-700"
                      }`}
                    >
                      {item.action || "(empty)"}
                    </span>
                  </td>

                  <td className="py-3">
                    {item.levelCode}
                  </td>

                  <td className="py-3">
                    {item.startDate}
                  </td>

                  <td className="py-3">
                    {item.endDate ?? "Current"}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      )}
    </div>
  );
};