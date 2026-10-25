import {
    useCallback,
    useEffect,
    useState,
} from "react";

import { residentContactApi } from "../api/resident-contact-api";
import type { ResidentContact } from "../types/resident-contact.types";

interface UseResidentContactsResult {
    contacts: ResidentContact[];
    loading: boolean;
    error: string | null;
    refetch: () => Promise<void>;
}

export function useResidentContacts(
    residentId: number,
): UseResidentContactsResult {
    const [contacts, setContacts] = useState<ResidentContact[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const fetchResidentContacts = useCallback(async () => {
        if (!Number.isInteger(residentId) || residentId <= 0) {
            setContacts([]);
            setError("Resident ID is invalid.");
            return;
        }

        try {
            setLoading(true);
            setError(null);

            const data =
                await residentContactApi.getResidentContacts(
                    residentId,
                );

            setContacts(data);
        } catch (error) {
            console.error(
                "Failed to fetch resident contacts:",
                error,
            );

            setContacts([]);

            setError(
                error instanceof Error
                    ? error.message
                    : "Unable to load resident contacts.",
            );
        } finally {
            setLoading(false);
        }
    }, [residentId]);

    useEffect(() => {
        void fetchResidentContacts();
    }, [fetchResidentContacts]);

    return {
        contacts,
        loading,
        error,
        refetch: fetchResidentContacts,
    };
}