import PageTitle from '@/components/seo/pageTitle';
import { Wallet } from 'lucide-react';
import { useEffect, useState } from 'react';
import usePageTitleStore from '@/contexts/usePageTitleStore.ts';
import { Icon } from '@tabler/icons-react';
import { useAuth } from '@/lib/auth.ts';
import apiService from '@/api/apiService.ts';
import apiRoutes from '@/api/apiRoutes.ts';
import { useNavigate } from 'react-router-dom';
import SalaryAccessoryTabs from '@/pages/app/payroll-management/salary-accessory/salary-accessory-tabs';
import useCompanyStore from "@/contexts/CompanyContext.ts";

import { EmployeeType } from '@/types/employee/EmployeeType.ts';

export function SalaryAccessoryPage() {
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
            .setTitle('Accessoire salaire', 'Indemnités et temps de travail', Wallet as Icon);
            setShowCompanySelect(true);
        fetchEmployeeList();
    }, []);

    return (
        <>
            <PageTitle title="Accessoire salaires" />

            {/* Sélecteur d'employé + bouton ajout employé */}
         {/*   <div className="w-full flex justify-center my-4">
                <div className="flex gap-4 items-center w-[600px]">
                    <Button
                        onClick={() => navigate(routeHelpers.dashboard.employee.create)}
                        className="whitespace-nowrap"
                        variant="default"
                    >
                        <Plus className="w-4 h-4 mr-2" />
                        Ajouter un employé
                    </Button>

                    {loading ? (
                        <Skeleton className="h-10 w-72 rounded-md" />
                    ) : employeeList.length === 0 ? (
                        <div className="text-center text-muted-foreground text-sm">
                            Aucun employé disponible.
                            <Button className="mt-2" onClick={fetchEmployeeList} variant="outline">
                                Réessayer
                            </Button>
                        </div>
                    ) : (
                        <EmployeeSelect
                            employees={employeeList}
                            selected={selectedEmployee}
                            onSelect={setSelectedEmployee}
                        />
                    )}
                </div>
            </div>*/}

            {/* Contenu administratif */}
            <div className="overflow-y-auto border-t border-gray-200">
                {selectedCompany ? (
                    <SalaryAccessoryTabs
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
