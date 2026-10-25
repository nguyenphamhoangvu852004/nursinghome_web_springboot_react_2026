import { useCallback, useEffect, useState } from "react";

import { contactApi } from "../api/contact-api";
import type { ContactListItem } from "../types/contact.types";

export const useContacts = (enabled = true) => {
    const [contacts, setContacts] = useState<ContactListItem[]>([]);
    const [search, setSearch] = useState("");
    const [includeDeleted, setIncludeDeleted] = useState(false);
    const [page, setPage] = useState(1);
    const [pageSize] = useState(10);
    const [total, setTotal] = useState(0);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const fetchContacts = useCallback(async () => {
        if (!enabled) {
            return;
        }

        try {
            setLoading(true);
            setError(null);

            const result = await contactApi.getContacts({
                search,
                includeDeleted,
                page,
                pageSize,
            });

            if (!result) {
                setContacts([]);
                setTotal(0);
                setError("Backend returned no contact data.");
                return;
            }

            setContacts(result.contacts ?? []);
            setTotal(result.meta?.total ?? 0);
        } catch (error) {
            console.error("Failed to fetch contacts:", error);

            setContacts([]);
            setTotal(0);

            setError(
                error instanceof Error
                    ? error.message
                    : "Unable to load contacts.",
            );
        } finally {
            setLoading(false);
        }
    }, [
        enabled,
        search,
        includeDeleted,
        page,
        pageSize,
    ]);

    useEffect(() => {
        if (!enabled) {
            return;
        }

        const timeoutId = window.setTimeout(() => {
            void fetchContacts();
        }, 350);

        return () => window.clearTimeout(timeoutId);
    }, [enabled, fetchContacts]);

    return {
        contacts,
        search,
        includeDeleted,
        page,
        pageSize,
        total,
        loading,
        error,
        setSearch,
        setIncludeDeleted,
        setPage,
        refetch: fetchContacts,
    };
};