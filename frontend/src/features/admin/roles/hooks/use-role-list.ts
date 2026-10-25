import { useQuery } from "@tanstack/react-query";
import { getRoleList } from "../services/role.service";

export const useRoleList = () => {
    return useQuery({
        queryKey: ["roles"],
        queryFn: getRoleList,
    });
};