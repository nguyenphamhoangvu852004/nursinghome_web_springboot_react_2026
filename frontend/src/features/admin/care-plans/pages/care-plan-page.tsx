import CarePlanOptions from "../components/care-plan-options";
import CarePlanTitle from "../components/care-plan-title";

import CarePlanStatistical from "../components/care-plan-statistical";
import CarePlanTable from "../components/care-plan-table";
import {
  getCarePlanList,
  searchCarePlans,
} from "@/services/care-plan/care-plan-services";
import { useEffect, useState } from "react";
import type { GetCarePlanListResponse } from "@/services/care-plan/care-plan-types";
const CarePlanPage = () => {
  const [loading, setLoading] = useState(false);
  const [carePlanResponse, setCarePlanResponse] =
    useState<GetCarePlanListResponse | null>(null);

  useEffect(() => {
    loadCarePlans();
  }, []);
  const handleSearch = async (residentName: any, status: any) => {
    try {
      setLoading(true);

      const response = await searchCarePlans(residentName, status);
      setCarePlanResponse(response);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };
  const loadCarePlans = async (residentName?: any, status?: any) => {
    try {
      setLoading(true);

      const response = status
        ? await searchCarePlans(residentName, status)
        : await getCarePlanList();

      setCarePlanResponse(response);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  if (!carePlanResponse) {
    return <div>Loading...</div>;
  }

  return (
    <div className="bg-gray-100 p-12">
      <CarePlanTitle />
      <CarePlanOptions onSearch={handleSearch} />

      <CarePlanStatistical
        carePlans={carePlanResponse.data.list}
        carePlanMetadata={carePlanResponse.metadata}
      />

      <CarePlanTable
        isLoading={loading}
        carePlans={carePlanResponse.data.list}
      />
    </div>
  );
};

export default CarePlanPage;
