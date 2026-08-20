import { create } from 'zustand';

interface TraitementStore {
    mois: string;
    companyId: number | null;

    setMoisEtCompany: (mois: string, companyId: number) => void;
    reset: () => void;
}

const useTraitementStore = create<TraitementStore>((set) => ({
    mois: '',
    companyId: null,

    setMoisEtCompany: (mois, companyId) => set({ mois, companyId }),
    reset: () => set({ mois: '', companyId: null }),
}));

export default useTraitementStore;
