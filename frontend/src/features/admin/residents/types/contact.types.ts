export interface ContactListItem {
    id: number;
    fullName: string;
    phonePrimary: string;
    email?: string | null;
    isDeleted: boolean;
    createdAt: string;
}

export interface ContactMeta {
    total: number;
    page: number;
    pageSize: number;
    totalPages: number;
    hasNext: boolean;
    hasPrevious: boolean;
}

export interface ContactListContainer {
    contacts: ContactListItem[];
    meta: ContactMeta;
}

export interface ContactDetail {
    id: number;

    firstName: string;
    middleName?: string | null;
    lastName: string;
    fullName: string;

    phonePrimary: string;
    phoneSecondary?: string | null;
    email?: string | null;

    addressId?: number | null;
    streetLine1?: string | null;
    streetLine2?: string | null;
    city?: string | null;
    state?: string | null;
    zipCode?: string | null;

    isDeleted: boolean;
    createdAt: string;
    updatedAt: string;
}

export interface CreateContactRequest {
    firstName: string;
    middleName?: string | null;
    lastName: string;
    phonePrimary: string;
    phoneSecondary?: string | null;
    email?: string | null;
    addressId?: number | null;
}

export type UpdateContactRequest = Partial<CreateContactRequest>;

export interface ResidentByContact {
    residentId: number;
    residentName: string;
    relationshipType: string;
    residentStatus: string;
    isPrimary: boolean;
    isEmergencyContact: boolean;
    isGuarantor: boolean;
}