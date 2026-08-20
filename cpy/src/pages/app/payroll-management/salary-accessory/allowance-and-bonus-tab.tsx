import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { UserPlusIcon, GiftIcon } from "lucide-react";
import StatCard from "@/components/useful/StatCard";
import { DynamicTable3 } from "@/components/tables/dynamic-table-3";
import { EmployeeSelect } from "@/components/useful/EmployeeSelect.tsx";
import { AllowanceAndBonusForm } from "./allowance-and-bonus-form.tsx";
import apiService from "@/api/apiService";
import apiRoutes from "@/api/apiRoutes";
import { useAuth } from "@/lib/auth";
import useCompanyStore from "@/contexts/CompanyContext";
import useEmployeeStore from "@/contexts/useEmployeeStore";

export default function AllowanceAndBonusTab() {
  const [isFormVisible, setIsFormVisible] = useState(false);
  const [data, setData] = useState<any[]>([]);
  const [filteredData, setFilteredData] = useState<any[]>([]);
  const { user } = useAuth();
  const { selectedCompany } = useCompanyStore();
  const { employees } = useEmployeeStore();

  const handleFilter = (query: string) => {
    const lowerQuery = query.trim().toLowerCase();
    if (!lowerQuery) {
      setFilteredData(data);
      return;
    }
    const result = data.filter((item) =>
      item.nom_agent?.toLowerCase().includes(lowerQuery) ||
      item.type?.toLowerCase().includes(lowerQuery)
    );
    setFilteredData(result);
  };

  useEffect(() => {
    const fetchData = async () => {
      if (!selectedCompany?.id) return;

      try {
        const response = await apiService.get(
          {
            url: `${apiRoutes.admin.app.contrat.rubriques.listByCompany}/${selectedCompany.id}`,
          },
          {
            userToken: `${user?.type} ${user?.token}`,
            hasNoSuccessModal: true,
          }
        );

        const apiData = response.data || [];

        const formatted = apiData.map((item: any) => {
          const employee = employees.find(e => e.employeId === item.employeId);
          const nom_agent = employee
            ? `${employee.prenom} ${employee.nom}`
            : `Employé #${item.employeId}`;

          return {
            nom_agent,
            mois: item.moisRubrique,
            type: item.rubriqueName,
            montant: item.montantRubrique,
          };
        });

        setData(formatted);
        setFilteredData(formatted);
      } catch (error) {
        console.error("Erreur lors du chargement des rubriques :", error);
      }
    };

    fetchData();
  }, [selectedCompany?.id, employees]);

  return (
    <div className="space-y-4 px-4">
      {!isFormVisible ? (
        <>
          {/* Header */}
          <div className="flex items-center justify-between">
            <div className="text-left">
              <h2 className="text-lg font-semibold text-black">Indemnités et primes</h2>
              <p className="text-xs text-muted-foreground">
                Liste des primes et retenues attribuées aux agents.
              </p>
            </div>
            <EmployeeSelect />
            <Button
              size="sm"
              className="flex items-center gap-2 rounded-md bg-primary text-primary-foreground hover:bg-primary/80"
              onClick={() => setIsFormVisible(true)}
            >
              <UserPlusIcon className="h-4 w-4 mr-2" />
              Ajouter une indemnité / prime
            </Button>
          </div>

          {/* Statistiques */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <StatCard
              icon={GiftIcon}
              title="Total lignes"
              value={filteredData.length}
              bgColor="bg-blue-100"
              textColor="text-blue-600"
            />
            <StatCard
              icon={GiftIcon}
              title="Total primes"
              value={filteredData.filter((e) => e.montant > 0).length}
              bgColor="bg-green-100"
              textColor="text-green-600"
            />
            <StatCard
              icon={GiftIcon}
              title="Total retenues"
              value={filteredData.filter((e) => e.montant < 0).length}
              bgColor="bg-red-100"
              textColor="text-red-600"
            />
          </div>

          {/* Tableau */}
          <div>
            <DynamicTable3
              columns={[
                {
                  key: "nom_agent",
                  label: "Agent",
                  render: (value) => (
                    <div className="flex items-center gap-2">
                      <p className="w-8 h-8 flex items-center justify-center rounded-full border text-sm font-medium bg-muted text-muted-foreground">
                        {value.toUpperCase().charAt(0)}
                      </p>
                      <span className="font-medium">{value}</span>
                    </div>
                  ),
                },
                { key: "mois", label: "Mois" },
                { key: "type", label: "Prime ou retenue" },
                {
                  key: "montant",
                  label: "Montant",
                  render: (val) => Number(val).toLocaleString() + " FCFA",
                },
              ]}
              data={filteredData}
              onAdd={() => {}}
              onFilter={handleFilter}
              filterPlaceholder="Rechercher par nom ou type..."
            />
          </div>
        </>
      ) : (
        <AllowanceAndBonusForm onCancel={() => setIsFormVisible(false)} />
      )}
    </div>
  );
}
