import { create } from "zustand";
import {EmployeeActifType} from "@/types/employee/EmployeeType";
import { CompanyType } from "@/types/company/CompanyType";
import apiRoutes from "@/api/apiRoutes";
import apiService from "@/api/apiService";

interface EmployeeStore {
    employees: EmployeeActifType[];
    loading: boolean;
    error: string | null;
    showEmployeeSelect: boolean;
    setEmployees: (employees: EmployeeActifType[]) => void;
    selectedEmployee: EmployeeActifType | null;
    setSelectedEmployee: (employee: EmployeeActifType | null) => void;
    resetEmployees: () => void;
    fetchEmployees: (company: CompanyType | null, user?: { type: string; token: string }) => Promise<void>;
    setShowEmployeeSelect: (show: boolean) => void;
}

const useEmployeeStore = create<EmployeeStore>((set) => ({
    employees: [],
    loading: false,
    error: null,
    selectedEmployee:null,
    showEmployeeSelect:true,

    setEmployees: (employees) => set({ employees, error: null }),

    // Sélectionner/désélectionner un employé
    setSelectedEmployee: (employee) => set({
        selectedEmployee: employee,
        // Cache automatiquement le sélecteur après sélection si nécessaire
        showEmployeeSelect: employee === null
    }),


    resetEmployees: () => set({ employees: [], error: null }),

    fetchEmployees: async (company, user) => {

        console.log("company",company)
        console.log("user",user)

        if (!company || typeof company !== 'object' || !company.id) {
            set({ employees: [], error: 'Entreprise invalide' });
            return;
        }

        try {
            set({ loading: true, error: null });
            if (!user || !user.token) {
                throw new Error('Authentification requise');
            }

            const response = await apiService.get(
                {
                    url: `${apiRoutes.admin.app.company.employees.listActif}/${company.id}`,
                },
                {
                    userToken: `${user?.type ?? ''} ${user?.token ?? ''}`,
                    hasNoSuccessModal: true,
                }
            );

            set({
                employees: response.data,
                loading: false
            });
        } catch (error) {
            console.error("Erreur de chargement des employés:");
            if (error instanceof Error) {
                apiService.handleError(error.message, { hasNoFailureModal: false });
            }
        }
    },

    setShowEmployeeSelect: (show) => set({ showEmployeeSelect: show })
}));

export default useEmployeeStore;