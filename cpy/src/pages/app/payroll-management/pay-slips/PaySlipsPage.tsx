import PageTitle from '@/components/seo/pageTitle';
import { FileText } from 'lucide-react';
import { useEffect, useState } from 'react';
import usePageTitleStore from '@/contexts/usePageTitleStore.ts';
import { Icon } from '@tabler/icons-react';
import { useAuth } from '@/lib/auth.ts';
import apiService from '@/api/apiService.ts';
import apiRoutes from '@/api/apiRoutes.ts';
import { useNavigate } from 'react-router-dom';
import { routeHelpers } from '@/helpers/routeHelpers';
import useCompanyStore from "@/contexts/CompanyContext.ts";
import { EmployeeType } from '@/types/employee/EmployeeType.ts';
import PayslipManager from "./payslips-manager-tab";

export function PaySlipsPage() {
    const { user } = useAuth();
    const navigate = useNavigate();
    const { setShowCompanySelect, selectedCompany } = useCompanyStore();
    const [loading, setLoading] = useState(true);
    const [employeeList, setEmployeeList] = useState<EmployeeType[]>([]);
    const [selectedEmployee, setSelectedEmployee] = useState<EmployeeType | null>(null);

    const fetchEmployeeList = async () => {
        try {
            setLoading(true);
            const response = await apiService.get(
                { url: apiRoutes.admin.app.employee.list },
                {
                    userToken: `${user?.type ?? ''} ${user?.token ?? ''}`,
                    hasNoSuccessModal: true,
                }
            );
            setEmployeeList(response.data);
        } catch (error) {
            console.error('Erreur de chargement des employés', error);
        } finally {
            setLoading(false);
        }
    };
    
    useEffect(() => {
        usePageTitleStore
            .getState()
            .setTitle('Edition des bulletins de paie', 'Consultation et génération des fiches de paie', FileText as Icon);
        setShowCompanySelect(true);
        fetchEmployeeList();
    }, []);

    return (
        <>
            <PageTitle title="Edition des états de salaire" />

            {/* Contenu principal */}
            <div className="overflow-y-auto border-t border-gray-200">
                {selectedCompany ? (
                    <div className="space-y-4 p-4">
                        <PayslipManager  />
                    </div>
                ) : (
                    <div className="p-4 text-center text-muted-foreground text-sm">
                        Sélectionnez une entreprise pour voir les bulletins de paie.
                    </div>
                )}
            </div>
        </>
    );
}