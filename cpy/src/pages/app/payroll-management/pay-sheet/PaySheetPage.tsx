import PageTitle from '@/components/seo/pageTitle';
import { Wallet, Plus } from 'lucide-react';
import { useEffect, useState } from 'react';
import usePageTitleStore from '@/contexts/usePageTitleStore.ts';
import { Icon } from '@tabler/icons-react';
import { Button } from '@/components/ui/button.tsx';
import { Skeleton } from '@/components/ui/skeleton.tsx';
import { useAuth } from '@/lib/auth.ts';
import apiService from '@/api/apiService.ts';
import apiRoutes from '@/api/apiRoutes.ts';
import { useNavigate } from 'react-router-dom';
import { routeHelpers } from '@/helpers/routeHelpers';
import BackupTabs from '@/pages/app/payroll-management/backup/backup-tabs';
import EmployeeSelect from '@/pages/app/payroll-management/salary-processing/employee-select';
import useCompanyStore from "@/contexts/CompanyContext.ts";

import { EmployeeType } from '@/types/employee/EmployeeType.ts';
import PaySheetTabs from './pay-sheet-tabs';

export function PaySheetPage() {
    const { user } = useAuth();
    const navigate = useNavigate();
    const {setShowCompanySelect,selectedCompany} = useCompanyStore();
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
            .setTitle('Edition des états de salaire', 'Analyse globale des états salariales', Wallet as Icon);
            setShowCompanySelect(true);
        fetchEmployeeList();
    }, []);

    return (
        <>
            <PageTitle title="Edition des états de salaire" />


            {/* Contenu administratif */}
            <div className="overflow-y-auto border-t border-gray-200">
                {selectedCompany ? (
                    <PaySheetTabs
                        company={selectedCompany}
                        setEmployee={() => setSelectedEmployee(null)}
                    />
                ) : (
                    <div className="p-4 text-center text-muted-foreground text-sm">
                        Sélectionnez un employé pour voir ses données de rémunération.
                    </div>
                )}
            </div>
        </>
    );
}
