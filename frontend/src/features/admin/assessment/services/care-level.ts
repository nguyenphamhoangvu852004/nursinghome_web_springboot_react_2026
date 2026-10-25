import { axiosInstance } from "@/lib/axios";
import type { CareLevelDTO } from "../types/care-level-type";

export const getCareLevels = async () => {
  const { data } = await axiosInstance.get<CareLevelDTO[]>("/admin/care-levels");
  console.log(data)
  return data;
};
