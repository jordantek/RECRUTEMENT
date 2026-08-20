import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { WalletIcon, CreditCardIcon } from "lucide-react";
import StatCard from "@/components/useful/StatCard";
import { DynamicTable3 } from "@/components/tables/dynamic-table-3";
import { EmployeeSelect } from "@/components/useful/EmployeeSelect.tsx";
import { AcompteForm } from "./acompte-form.tsx";
import apiService from "@/api/apiService";
import apiRoutes from "@/api/apiRoutes";
import { useAuth } from "@/lib/auth";
import useCompanyStore from "@/contexts/CompanyContext";
import useEmployeeStore from "@/contexts/useEmployeeStore";

export default function AcompteTab() {
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
      item.mois?.toLowerCase().includes(lowerQuery)
    );
    setFilteredData(result);
  };

  useEffect(() => {
    const fetchData = async () => {
      if (!selectedCompany?.id) return;

      try {
        const response = await apiService.get(
          {
            url: apiRoutes.admin.app.acompte.listByCompany(selectedCompany.id)  
          },
        
          { userToken: `${user?.type} ${user?.token}`, hasNoSuccessModal: true }
        );

        const apiData = response.data || [];
        const formatted = apiData.map((item: any) => {
          const employee = employees.find(e => e.employeId === item.employeId);
          const nom_agent = employee 
            ? `${employee.prenom} ${employee.nom}` 
            : `Employé #${item.employeId}`;

          return {
            id: item.id,
            nom_agent,
            mois: item.mois,
            montant: item.montant,
            solde: item.solde || '-',
            salaireNet: item.salaireNet || '-'
          };
        });

        setData(formatted);
        setFilteredData(formatted);
      } catch (error) {
        console.error("Erreur lors du chargement des acomptes :", error);
      }
    };

    fetchData();
  }, [selectedCompany?.id, employees]);

  return (
    <div className="space-y-4">
      {!isFormVisible ? (
        <>
          {/* Header */}
          <div className="flex items-center justify-between">
            <div className="text-left">
              <h2 className="text-lg font-semibold text-black">Gestion des acomptes</h2>
              <p className="text-xs text-muted-foreground">
                Liste des acomptes versés aux employés.
              </p>
            </div>
            <EmployeeSelect />
            <Button
              size="sm"
              className="flex items-center gap-2 rounded-md bg-primary text-primary-foreground hover:bg-primary/80"
              onClick={() => setIsFormVisible(true)}
            >
              <CreditCardIcon className="h-4 w-4 mr-2" />
              Nouvel acompte
            </Button>
          </div>

          {/* Statistiques */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <StatCard
              icon={WalletIcon}
              title="Total acomptes"
              value={filteredData.length}
              bgColor="bg-blue-100"
              textColor="text-blue-600"
            />
            <StatCard
              icon={WalletIcon}
              title="Montant total"
              value={filteredData.reduce((sum, item) => sum + (item.montant || 0), 0).toLocaleString() + " FCFA"}
              bgColor="bg-green-100"
              textColor="text-green-600"
            />
            <StatCard
              icon={WalletIcon}
              title="Solde moyen"
              value={(filteredData.reduce((sum, item) => sum + (item.solde || 0), 0) / (filteredData.length || 1)).toLocaleString() + " FCFA"}
              bgColor="bg-purple-100"
              textColor="text-purple-600"
            />
          </div>

          {/* Tableau */}
          <div>
            <DynamicTable3
              columns={[
                {
                  key: "nom_agent",
                  label: "Employé",
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
                { 
                  key: "montant", 
                  label: "Montant", 
                  render: (val) => Number(val).toLocaleString() + " FCFA" 
                },
                { 
                  key: "solde", 
                  label: "Solde", 
                  render: (val) => val === '-' ? '-' : Number(val).toLocaleString() + " FCFA" 
                },
                { 
                  key: "salaireNet", 
                  label: "Salaire net", 
                  render: (val) => val === '-' ? '-' : Number(val).toLocaleString() + " FCFA" 
                },
              ]}
              data={filteredData}
              onAdd={() => {}}
              onFilter={handleFilter}
              filterPlaceholder="Rechercher par nom ou mois..."
            />
          </div>
        </>
      ) : (
        <AcompteForm onCancel={() => setIsFormVisible(false)} />
      )}
    </div>
  );
}