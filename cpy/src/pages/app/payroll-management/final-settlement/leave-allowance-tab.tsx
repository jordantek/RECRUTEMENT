import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { UserPlusIcon, CalendarIcon } from "lucide-react";
import StatCard from "@/components/useful/StatCard";
import { DynamicTable3 } from "@/components/tables/dynamic-table-3";
import { EmployeeSelect } from "@/components/useful/EmployeeSelect.tsx";
import { LeaveAllowanceForm } from "./leave-allowance-form.tsx";
import apiService from "@/api/apiService";
import apiRoutes from "@/api/apiRoutes";
import { useAuth } from "@/lib/auth";
import useCompanyStore from "@/contexts/CompanyContext";

export default function LeaveAllowanceTab() {
  const [isFormVisible, setIsFormVisible] = useState(false);
  const [data, setData] = useState<any[]>([]);
  const { user } = useAuth();
  const { selectedCompany } = useCompanyStore();

  return (
    <div className="space-y-4 px-4">
      {!isFormVisible ? (
        <>
          {/* Header */}
          <div className="flex items-center justify-between">
            <div className="text-left">
              <h2 className="text-lg font-semibold text-black">Indemnités de congés</h2>
              <p className="text-xs text-muted-foreground">
                Gestion des indemnités de congés des agents.
              </p>
            </div>
            <Button
              size="sm"
              className="flex items-center gap-2 rounded-md bg-primary text-primary-foreground hover:bg-primary/80"
              onClick={() => setIsFormVisible(true)}
            >
              <UserPlusIcon className="h-4 w-4 mr-2" />
              Calculer une indemnité
            </Button>
          </div>

          {/* Statistiques */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <StatCard
              icon={CalendarIcon}
              title="Total allocations"
              value={data.filter(e => e.typeOperation === 'ALLOCATION').length}
              bgColor="bg-blue-100"
              textColor="text-blue-600"
            />
            <StatCard
              icon={CalendarIcon}
              title="Total indemnités"
              value={data.filter(e => e.typeOperation === 'INDEMNITE_CONGE').length}
              bgColor="bg-green-100"
              textColor="text-green-600"
            />
          </div>

          {/* Tableau */}
          <div>
            <DynamicTable3
              columns={[
                {
                  key: "employe",
                  label: "Agent",
                  render: (value) => (
                    <div className="flex items-center gap-2">
                      <p className="w-8 h-8 flex items-center justify-center rounded-full border text-sm font-medium bg-muted text-muted-foreground">
                        {value.nom.charAt(0)}{value.prenom.charAt(0)}
                      </p>
                      <span className="font-medium">{value.prenom} {value.nom}</span>
                    </div>
                  ),
                },
                { key: "mois", label: "Mois" },
                { 
                  key: "typeOperation", 
                  label: "Type",
                  render: (val) => val === 'ALLOCATION' ? 'Allocation' : 'Indemnité' 
                },
                {
                  key: "montantTotal",
                  label: "Montant",
                  render: (val) => Number(val).toLocaleString() + " FCFA",
                },
              ]}
              data={data}
              onAdd={() => setIsFormVisible(true)}
            />
          </div>
        </>
      ) : (
        <LeaveAllowanceForm 
          onCancel={() => setIsFormVisible(false)}
          onSuccess={(newData) => {
            setData(prev => [...prev, newData]);
            setIsFormVisible(false);
          }}
        />
      )}
    </div>
  );
}