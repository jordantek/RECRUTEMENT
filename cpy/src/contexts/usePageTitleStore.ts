// stores/usePageTitleStore.ts
import { create } from 'zustand';
import {Icon} from "@tabler/icons-react";
 // Assurez-vous que lucide-react est installé

interface PageTitleStore {
    title: string;
    description: string;
    icon: Icon | null; // Icône facultatif
    setTitle: (title: string, description?: string, icon?: Icon | null) => void;
    resetTitle: () => void;
}

const usePageTitleStore = create<PageTitleStore>((set) => ({
    title: 'Page Title',
    description: 'Page description',
    icon: null,
    setTitle: (title, description = 'Page description', icon = null) =>
        set({ title, description, icon }),
    resetTitle: () => set({ title: 'Page Title', description: 'Page description', icon: null }),
}));

export default usePageTitleStore;
