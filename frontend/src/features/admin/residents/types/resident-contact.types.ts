export interface ResidentContact {
    residentContactId: number;
    residentId: number;
    contactId: number;

    firstName: string;
    middleName: string | null;
    lastName: string;
    fullName: string;

    phonePrimary: string;
    phoneSecondary: string | null;
    email: string | null;
    addressId: number | null;

    relationshipType: string;

    isPrimary: boolean;
    isEmergencyContact: boolean;
    isGuarantor: boolean;

    financialResponsibilityPct: number;
    createdAt: string;
}

export interface CreateResidentContactRequest {
    contactId: number;
    relationshipType: string;
    isPrimary: boolean;
    isEmergencyContact: boolean;
    isGuarantor: boolean;
    financialResponsibilityPct: number;
}

export type ResidentContactRelationshipFormData =
    Omit<CreateResidentContactRequest, "contactId">;

export interface UpdateResidentContactRequest {
    relationshipType?: string;
    isPrimary?: boolean;
    isEmergencyContact?: boolean;
    isGuarantor?: boolean;
    financialResponsibilityPct?: number;
}

export interface ResidentContactApiResponse
    extends Omit<
        ResidentContact,
        "isPrimary" | "isEmergencyContact" | "isGuarantor"
    > {
    isPrimary?: boolean;
    isEmergencyContact?: boolean;
    isGuarantor?: boolean;

    primary?: boolean;
    emergencyContact?: boolean;
    guarantor?: boolean;
}

export interface ResidentQuickContact {
    id: number;
    firstName: string;
    isPrimary: boolean;
    isGuarantor: boolean;
}