import PageTitle from '@/components/seo/pageTitle';
import { Wallet } from 'lucide-react';
import { useEffect, useState } from 'react';
import usePageTitleStore from '@/contexts/usePageTitleStore.ts';
import { Icon } from '@tabler/icons-react';
import { useAuth } from '@/lib/auth.ts';
import apiService from '@/api/apiService.ts';
import apiRoutes from '@/api/apiRoutes.ts';
import BackupTabs from '@/pages/app/payroll-management/backup/backup-tabs';
import useCompanyStore from "@/contexts/CompanyContext.ts";

import { EmployeeType } from '@/types/employee/EmployeeType.ts';

export function BackupPage() {
    const { user } = useAuth();
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
            .setTitle('Calcul et sauvegarde des salaires', 'Analyse globale des charges salariales', Wallet as Icon);
            setShowCompanySelect(true);
        fetchEmployeeList();
    }, []);

    return (
        <>
            <PageTitle title="Calcul et sauvegarde des salaires" />

            {/* Contenu administratif */}
            <div className="overflow-y-auto border-t border-gray-200">
                {selectedCompany ? (
                    <BackupTabs
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
