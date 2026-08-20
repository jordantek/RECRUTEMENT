// modalStore.ts (zustand store)
import create from "zustand";

interface ModalStore {
  showModal: boolean;
  setShowModal: (value: boolean) => void;
}

export const useModalStore = create<ModalStore>((set) => ({
  showModal: false,
  setShowModal: (value) => set({ showModal: value }),
}));
