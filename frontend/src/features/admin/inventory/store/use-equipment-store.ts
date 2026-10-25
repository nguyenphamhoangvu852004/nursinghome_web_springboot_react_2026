import { create } from "zustand";

import type { EquipmentStatus } from "../api/equipment-api";

export type EquipmentScreen = "list" | "create" | "detail" | "update";

export interface EquipmentItem {
	id: number;
	itemName: string;
	category: {
		id: number;
		name: string;
	};
	assetTag: string;
	status: EquipmentStatus;
	facility: {
		id: number;
		name: string;
	};
	unitValue: string;
}

export interface EquipmentFormValues {
	itemName: string;
	categoryId: string;
	assetTag: string;
	facilityId: string;
	unitValue: string;
}

interface EquipmentState {
	screen: EquipmentScreen;
	selectedEquipmentId: number | null;
	selectedEquipment: EquipmentItem | null;
	formValues: EquipmentFormValues;
	searchTerm: string;
	statusFilter: EquipmentStatus | "ALL";
	errorMessage: string | null;
	deleteDialogOpen: boolean;
	setScreen: (screen: EquipmentScreen) => void;
	setSelectedEquipmentId: (id: number | null) => void;
	setSelectedEquipment: (equipment: EquipmentItem | null) => void;
	setFormValues: (values: EquipmentFormValues) => void;
	setSearchTerm: (value: string) => void;
	setStatusFilter: (value: EquipmentStatus | "ALL") => void;
	setErrorMessage: (message: string | null) => void;
	openList: () => void;
	openCreate: () => void;
	openDetail: (equipment: EquipmentItem) => void;
	openUpdate: () => void;
	openDeleteDialog: () => void;
	closeDeleteDialog: () => void;
	resetEquipmentState: () => void;
}

const emptyFormValues: EquipmentFormValues = {
	itemName: "",
	categoryId: "",
	assetTag: "",
	facilityId: "",
	unitValue: "",
};

export const useEquipmentStore = create<EquipmentState>((set, get) => ({
	screen: "list",
	selectedEquipmentId: null,
	selectedEquipment: null,
	formValues: emptyFormValues,
	searchTerm: "",
	statusFilter: "ALL",
	errorMessage: null,
	deleteDialogOpen: false,

	setScreen: (screen) => set({ screen }),
	setSelectedEquipmentId: (selectedEquipmentId) => set({ selectedEquipmentId }),
	setSelectedEquipment: (selectedEquipment) => set({ selectedEquipment }),
	setFormValues: (formValues) => set({ formValues }),
	setSearchTerm: (searchTerm) => set({ searchTerm }),
	setStatusFilter: (statusFilter) => set({ statusFilter }),
	setErrorMessage: (errorMessage) => set({ errorMessage }),

	openList: () =>
		set({
			screen: "list",
			selectedEquipmentId: null,
			selectedEquipment: null,
			formValues: emptyFormValues,
			errorMessage: null,
			deleteDialogOpen: false,
		}),

	openCreate: () =>
		set({
			screen: "create",
			selectedEquipmentId: null,
			selectedEquipment: null,
			formValues: emptyFormValues,
			errorMessage: null,
			deleteDialogOpen: false,
		}),

	openDetail: (equipment) =>
		set({
			screen: "detail",
			selectedEquipmentId: equipment.id,
			selectedEquipment: equipment,
			errorMessage: null,
			deleteDialogOpen: false,
		}),

	openUpdate: () => {
		const selectedEquipment = get().selectedEquipment;

		if (!selectedEquipment) {
			return;
		}

		set({
			screen: "update",
			selectedEquipmentId: selectedEquipment.id,
			formValues: {
				itemName: selectedEquipment.itemName,
				categoryId: String(selectedEquipment.category.id),
				assetTag: selectedEquipment.assetTag,
				facilityId: String(selectedEquipment.facility.id),
				unitValue: selectedEquipment.unitValue,
			},
			errorMessage: null,
		});
	},

	openDeleteDialog: () => set({ deleteDialogOpen: true }),
	closeDeleteDialog: () => set({ deleteDialogOpen: false }),

	resetEquipmentState: () =>
		set({
			screen: "list",
			selectedEquipmentId: null,
			selectedEquipment: null,
			formValues: emptyFormValues,
			errorMessage: null,
			deleteDialogOpen: false,
		}),
}));
