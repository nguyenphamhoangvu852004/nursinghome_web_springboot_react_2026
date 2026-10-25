import { useMutation } from "@tanstack/react-query";
import { seedDemoData } from "../services/demo-data.service";

export const useSeedDemoData = () => {
    return useMutation({
        mutationFn: seedDemoData,
    });
};