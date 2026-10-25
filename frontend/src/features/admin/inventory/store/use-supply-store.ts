import { create } from 'zustand';

import type { SupplyStatus } from '../api/supply-api';

export type SupplyScreen = 'list' | 'create' | 'detail' | 'update';

export interface SupplyItem {
    id: number;
    itemName: string;
    category: {
        id: number;
        name: string;
    };
    facility: {
        id: number;
        name: string;
    };
    stockOnHand: number;
    total: number;
    reorderThreshold: string;
    unitCost: string;
    privatePayRate: string;
    status: SupplyStatus;
}

export interface SupplyFormValues {
    itemName: string;
    categoryId: string;
    facilityId: string;
    stockOnHand:number;
    reorderThreshold: string;
    unitCost: string;
    privatePayRate: string;
}

interface SupplyState {
    screen: SupplyScreen;
    selectedSupplyId: number | null;
    selectedSupply: SupplyItem | null;
    formValues: SupplyFormValues;
    searchTerm: string;
    statusFilter: SupplyStatus | 'ALL';
    errorMessage: string | null;
    deleteDialogOpen: boolean;
    setScreen: (screen: SupplyScreen) => void;
    setSelectedSupplyId: (id: number | null) => void;
    setSelectedSupply: (supply: SupplyItem | null) => void;
    setFormValues: (values: SupplyFormValues) => void;
    setSearchTerm: (value: string) => void;
    setStatusFilter: (value: SupplyStatus | 'ALL') => void;
    setErrorMessage: (message: string | null) => void;
    openList: () => void;
    openCreate: () => void;
    openDetail: (supply: SupplyItem) => void;
    openUpdate: () => void
    openDeleteDialog: () => void;
    closeDeleteDialog: () => void;
    resetSupplyState: () => void;
}

const emptyFormValues: SupplyFormValues = {
    itemName: '',
    categoryId: '',
    facilityId: '',
    stockOnHand:0,
    reorderThreshold: '',
    unitCost: '',
    privatePayRate: '',
}

export const useSupplyStore = create<SupplyState>((set, get) => ({
    screen: "list",
    selectedSupplyId: null,
    selectedSupply: null,
    formValues: emptyFormValues,
    searchTerm: "",
    statusFilter: "ALL",
    errorMessage: null,
    deleteDialogOpen: false,

    setScreen: (screen) => set({ screen }),
    setSelectedSupplyId: (selectedSupplyId) => set({ selectedSupplyId }),
    setSelectedSupply: (selectedSupply) => set({ selectedSupply }),
    setFormValues: (formValues) => set({ formValues }),
    setSearchTerm: (searchTerm) => set({ searchTerm }),
    setStatusFilter: (statusFilter) => set({ statusFilter }),
    setErrorMessage: (errorMessage) => set({ errorMessage }),

    openList: () =>
        set({
            screen: 'list',
            selectedSupplyId: null,
            selectedSupply: null,
            formValues: emptyFormValues,
            errorMessage: null,
            deleteDialogOpen: false,
        }),

    openCreate: () =>
        set({
            screen: 'create',
            selectedSupplyId: null,
            selectedSupply: null,
            formValues: emptyFormValues,
            errorMessage: null,
            deleteDialogOpen: false,
        }),
    openDetail: (supply) =>
        set({
            screen: 'detail',
            selectedSupplyId: supply.id,
            selectedSupply: supply,
            errorMessage: null,
            deleteDialogOpen: false,
        })
        ,
    openUpdate: () => {
        const selectedSupply = get().selectedSupply;

        if (!selectedSupply) {
            return;
        }

        set({
            screen: 'update',
            selectedSupplyId: selectedSupply.id,
            formValues: {
                itemName: selectedSupply.itemName,
                categoryId: selectedSupply.category.id.toString(),
                facilityId: selectedSupply.facility.id.toString(),
                stockOnHand: selectedSupply.stockOnHand,
                reorderThreshold: selectedSupply.reorderThreshold,
                unitCost: selectedSupply.unitCost,
                privatePayRate: selectedSupply.privatePayRate,
            },
            errorMessage: null,
        });

    },

    openDeleteDialog: () => set({ deleteDialogOpen: true }),
    closeDeleteDialog: () => set({ deleteDialogOpen: false }),

    resetSupplyState: () =>
        set({
            screen: 'list',
            selectedSupplyId: null,
            selectedSupply: null,
            formValues: emptyFormValues,
            errorMessage: null,
            deleteDialogOpen: false,
        }),
}));