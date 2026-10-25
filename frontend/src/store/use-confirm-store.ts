import { create } from "zustand";
import type { ReactNode } from "react";

interface ConfirmStore {
  isOpen: boolean;
  title?: ReactNode;
  description?: ReactNode;
  confirmText?: string;
  cancelText?: string;
  onConfirm?: () => void;
  open: (opts: {
    title?: ReactNode;
    description?: ReactNode;
    confirmText?: string;
    cancelText?: string;
    onConfirm: () => void;
  }) => void;
  close: () => void;
}

export const useConfirmStore = create<ConfirmStore>((set) => ({
  isOpen: false,
  open: (opts) => set({ isOpen: true, ...opts }),
  close: () =>
    set({
      isOpen: false,
      title: undefined,
      description: undefined,
      onConfirm: undefined,
    }),
}));
