// stores/useCompanyStore.ts
import { create } from "zustand"
import {CompanyType} from "@/types/company/CompanyType.ts";
import apiRoutes from "@/api/apiRoutes.ts";
import apiService from "@/api/apiService.ts";

interface CompanyStore {
    companies: CompanyType[]
    selectedCompany: CompanyType | null
    loading: boolean
    setCompanies: (companies: CompanyType[]) => void
    selectCompany: (company: CompanyType) => void
    resetCompany: () => void
    fetchCompanyList: (user?: { type: string; token: string }) => Promise<void>
    showCompanySelect: boolean
    setShowCompanySelect: (show: boolean) => void
}


const useCompanyStore = create<CompanyStore>((set) => ({
    companies: [],
    selectedCompany: null,
    loading: false,

    setCompanies: (companies) => set({ companies }),

    selectCompany: (company) => set({ selectedCompany: company }),

    resetCompany: () => set({ selectedCompany: null }),

    fetchCompanyList: async (user) => {
        try {
            set({ loading: true })

            const response = await apiService.get(
                { url: apiRoutes.admin.app.company.list },
                {
                    userToken: `${user?.type ?? ''} ${user?.token ?? ''}`,
                    hasNoSuccessModal: true,
                }
            )

            const companies = response.data
            set({ companies })

            if (companies.length > 0) {
                set({ selectedCompany: companies[0] })
            }
        } catch (error) {
            console.error("Erreur de chargement des entreprises", error)
        } finally {
            set({ loading: false })
        }
    },

    showCompanySelect: false,
    setShowCompanySelect: (show) => set({ showCompanySelect: show })

}))

export default useCompanyStore
