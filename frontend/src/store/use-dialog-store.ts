import { create } from "zustand";
import type { ReactNode } from "react";

interface DialogStore {
  isOpen: boolean;
  title?: ReactNode;
  description?: ReactNode;
  content?: ReactNode;
  open: (opts: {
    title?: ReactNode;
    description?: ReactNode;
    content?: ReactNode;
  }) => void;
  close: () => void;
}

export const useDialogStore = create<DialogStore>((set) => ({
  isOpen: false,
  open: (opts) => set({ isOpen: true, ...opts }),
  close: () =>
    set({
      isOpen: false,
      title: undefined,
      description: undefined,
      content: undefined,
    }),
}));
