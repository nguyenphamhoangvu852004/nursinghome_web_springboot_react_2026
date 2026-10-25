import { create } from "zustand";

export type CategoryScreen = "list" | "create" | "detail" | "update";

export interface CategoryItem {
	id: number;
	categoryName: string;
	description: string;
	createdAt: string;
}

export interface CategoryFormValues {
	categoryName: string;
	description: string;
}

interface InventoryCategoryState {
	screen: CategoryScreen;
	selectedCategoryId: number | null;
	selectedCategory: CategoryItem | null;
	formValues: CategoryFormValues;
	errorMessage: string | null;
	deleteDialogOpen: boolean;
	setScreen: (screen: CategoryScreen) => void;
	setSelectedCategoryId: (id: number | null) => void;
	setSelectedCategory: (category: CategoryItem | null) => void;
	openList: () => void;
	openCreate: () => void;
	openDetail: (category: CategoryItem) => void;
	openUpdate: () => void;
	setFormValues: (values: CategoryFormValues) => void;
	setErrorMessage: (message: string | null) => void;
	openDeleteDialog: () => void;
	closeDeleteDialog: () => void;
	resetCategoryState: () => void;
}

const emptyFormValues: CategoryFormValues = {
	categoryName: "",
	description: "",
};

export const useInventoryCategoryStore = create<InventoryCategoryState>((set, get) => ({
	screen: "list",
	selectedCategoryId: null,
	selectedCategory: null,
	formValues: emptyFormValues,
	errorMessage: null,
	deleteDialogOpen: false,

	setScreen: (screen) => set({ screen }),
	setSelectedCategoryId: (selectedCategoryId) => set({ selectedCategoryId }),
	setSelectedCategory: (category) => set({ selectedCategory: category }),

	openList: () =>
		set({
			screen: "list",
			selectedCategoryId: null,
			selectedCategory: null,
			formValues: emptyFormValues,
			errorMessage: null,
			deleteDialogOpen: false,
		}),

	openCreate: () =>
		set({
			screen: "create",
			selectedCategoryId: null,
			selectedCategory: null,
			formValues: emptyFormValues,
			errorMessage: null,
			deleteDialogOpen: false,
		}),

	openDetail: (category) =>
		set({
			screen: "detail",
			selectedCategoryId: category.id,
			selectedCategory: category,
			errorMessage: null,
			deleteDialogOpen: false,
		}),

	openUpdate: () => {
		const selectedCategory = get().selectedCategory;

		if (!selectedCategory) {
			return;
		}

		set({
			screen: "update",
				selectedCategoryId: selectedCategory.id,
			formValues: {
				categoryName: selectedCategory.categoryName,
				description: selectedCategory.description,
			},
			errorMessage: null,
		});
	},

	setFormValues: (values) => set({ formValues: values }),

	setErrorMessage: (message) => set({ errorMessage: message }),

	openDeleteDialog: () => set({ deleteDialogOpen: true }),
	closeDeleteDialog: () => set({ deleteDialogOpen: false }),

	resetCategoryState: () =>
		set({
			screen: "list",
			selectedCategoryId: null,
			selectedCategory: null,
			formValues: emptyFormValues,
			errorMessage: null,
			deleteDialogOpen: false,
		}),
}));

