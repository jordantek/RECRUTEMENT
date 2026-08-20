import PageTitle from '@/components/seo/pageTitle';
import {
  BadgeAlert,
  SearchIcon,
  UsersIcon,
} from 'lucide-react';
import { useEffect, useState } from 'react';
import usePageTitleStore from '@/contexts/usePageTitleStore.ts';
import { Icon } from '@tabler/icons-react';
import { Input } from '@/components/ui/input.tsx';
import { Button } from '@/components/ui/button.tsx';
import { Skeleton } from '@/components/ui/skeleton.tsx';
import { EmployeeType } from '@/types/employee/EmployeeType.ts';
import apiService from '@/api/apiService.ts';
import apiRoutes from '@/api/apiRoutes.ts';
import { useAuth } from '@/lib/auth.ts';
import EmployeeItem from "@/components/layout/employee/employee-item.tsx";
import EmployeeGeneralTabs from "@/pages/app/employee/employee-general-tabs.tsx";
import useCompanyStore from "@/contexts/CompanyContext.ts";

export function EmployeePage() {
  const { user } = useAuth();
  const {setShowCompanySelect} = useCompanyStore();
  const [inputValue, setInputValue] = useState('');
  const [employeeList, setEmployeeList] = useState<EmployeeType[]>([]);
  const [selectedEmployee, setSelectedEmployee] = useState<EmployeeType | null>(null);
  const [loadingEmployees, setLoadingEmployees] = useState(false);

  const fetchEmployeeList = async () => {
    try {
      setLoadingEmployees(true);
      const response = await apiService.get(
          { url: apiRoutes.admin.app.employee.list },
          {
            userToken: `${user?.type ?? ''} ${user?.token ?? ''}`,
            hasNoSuccessModal: true,
          }
      );
      setEmployeeList(response.data.content);
      if (response.data.length > 0) {
        setSelectedEmployee(response.data[0]);
      }
    } catch (error) {
      console.error('Erreur de chargement des employés', error);
    } finally {
      setLoadingEmployees(false);
    }
  };

  useEffect(() => {
    usePageTitleStore
        .getState()
        .setTitle('Employés', 'Gestion des employés', UsersIcon as Icon);
    fetchEmployeeList();
    setShowCompanySelect(false);
  }, []);

  // 🔍 Filtrage par nom
  const filteredEmployees = employeeList.filter((employee) =>
      (employee.nom || '').toLowerCase().includes(inputValue.toLowerCase())
  );

  return (
      <>
        <PageTitle title="Employés" />

        <div className="h-[calc(100vh-64px)] rounded overflow-hidden bg-background grid grid-cols-12 gap-0 p-0">
          {/* Colonne gauche */}
          <div className="col-span-3 pe-1 ps-1 overflow-y-auto scroll-hidden">
            <div className="w-full mb-5">
              <div className="relative mt-2">
                <Input
                    className="w-full peer ps-9 pe-9"
                    placeholder="Rechercher"
                    type="search"
                    value={inputValue}
                    onChange={(e) => setInputValue(e.target.value)}
                />
                <div className="text-muted-foreground/80 pointer-events-none absolute inset-y-0 start-0 flex items-center justify-center ps-3 peer-disabled:opacity-50">
                  <SearchIcon size={16} aria-hidden="true" />
                </div>
              </div>
            </div>

            {loadingEmployees ? (
                <div className="space-y-2 px-4">
                  {Array.from({ length: 6 }).map((_, i) => (
                      <div key={i} className="p-4 space-y-2">
                        <Skeleton className="h-4 w-2/3" />
                        <Skeleton className="h-3 w-1/3" />
                      </div>
                  ))}
                </div>
            ) : employeeList.length === 0 ? (
                <div className="p-6 text-center text-muted-foreground flex flex-col items-center justify-center h-full space-y-4">
                  <BadgeAlert size={55} />
                  <h2 className="text-xl font-semibold text-gray-700">Aucun employé à afficher</h2>
                  <p className="text-xs text-gray-500 max-w-md">
                    Il est possible qu'aucun employé ne soit encore enregistré ou qu'une erreur soit survenue lors du
                    chargement des données.
                  </p>
                  <Button className="mt-2" onClick={fetchEmployeeList} variant="outline">
                    Réessayer
                  </Button>
                </div>
            ) : filteredEmployees.length === 0 ? (
                <div className="px-4 py-2 text-sm text-center text-muted-foreground">
                  Aucun employé ne correspond à votre recherche.
                </div>
            ) : (
                filteredEmployees.map((employee) => (
                    <EmployeeItem
                        key={employee.id}
                        employee={employee}
                        isActive={selectedEmployee?.id === employee.id}
                        onClick={() => setSelectedEmployee(employee)}
                    />
                ))
            )}
          </div>
          
          {/* Colonne droite */}
          <div className="overflow-y-auto col-span-9 border-l border-gray-200">
            <EmployeeGeneralTabs employee={selectedEmployee} setEmployee={setSelectedEmployee}/>
          </div>
        </div>
      </>

  );
}
