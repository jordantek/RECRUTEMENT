import { create } from "zustand";

type TabsState = {
    [groupKey: string]: string | null;
};

type TabsStore = {
    activeTabs: TabsState;
    setActiveTab: (groupKey: string, tab: string) => void;
    getActiveTab: (groupKey: string) => string | null;
    resetTab: (groupKey: string) => void;
};

const useTabsStore = create<TabsStore>((set, get) => ({
    activeTabs: {},
    setActiveTab: (groupKey, tab) =>
        set((state) => ({
            activeTabs: {
                ...state.activeTabs,
                [groupKey]: tab,
            },
        })),
    getActiveTab: (groupKey) => get().activeTabs[groupKey] ?? null,
    resetTab: (groupKey) =>
        set((state) => {
            const { [groupKey]: _, ...rest } = state.activeTabs;
            return { activeTabs: rest };
        }),
}));

export default useTabsStore;
