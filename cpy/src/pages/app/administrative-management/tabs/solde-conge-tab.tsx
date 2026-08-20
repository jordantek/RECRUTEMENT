import { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";

import { Button } from "@/components/ui/button";
import { DynamicTable3 } from "@/components/tables/dynamic-table-3";
import { RefreshCw } from "lucide-react";

import apiService from "@/api/apiService";
import apiRoutes from "@/api/apiRoutes";
import { useAuth } from "@/lib/auth";
import useCompanyStore from "@/contexts/CompanyContext";
import useEmployeeStore from "@/contexts/useEmployeeStore";

type SoldeConge = {
  id?: number;
  employeId: number;
  nomComplet: string;
  dateReference: string;
  creditConge: number;
  joursPris: number;
  solde: number;
};

export default function SoldeCongeTab() {
  const { user } = useAuth();
  const { selectedCompany } = useCompanyStore();
  const { employees, fetchEmployees } = useEmployeeStore();
  const [soldes, setSoldes] = useState<SoldeConge[]>([]);
  const [filteredData, setFilteredData] = useState<SoldeConge[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (selectedCompany && user) {
      fetchEmployees(selectedCompany, user);
    }
  }, [selectedCompany, user]);

  const fetchSoldes = async () => {
    if (!selectedCompany?.id) return;

    setLoading(true);
    try {
      const response = await apiService.get(
        {
          url: apiRoutes.admin.app.employee.creditConge.soldeConges(
            selectedCompany.id
          ),
        },
        {
          userToken: `${user?.type} ${user?.token}`,
          hasNoSuccessModal: true,
        }
      );

      const data = response.data || [];
      const formatted: SoldeConge[] = data.map((item: any) => ({
        employeId: item.employe.id,
        nomComplet: `${item.employe.prenom ?? ""} ${item.employe.nom ?? ""}`,
        dateReference: item.dateReferenceCreditConge,
        creditConge: item.creditConge,
        joursPris: item.nombreTotalJourPris,
        solde: item.soldeConge,
      }));

      setSoldes(formatted);
      setFilteredData(formatted);
    } catch (error) {
      console.error("Erreur lors du chargement des soldes de congé:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSoldes();
  }, [selectedCompany?.id]);

  const handleFilter = (query: string) => {
    const lower = query.trim().toLowerCase();
    if (!lower) return setFilteredData(soldes);
    setFilteredData(
      soldes.filter((s) => s.nomComplet.toLowerCase().includes(lower))
    );
  };

  const renderSoldeBadge = (value: number) => {
    const isPositive = value >= 0;
    const badgeStyle = isPositive
      ? "bg-green-100 text-green-700 border-green-200"
      : "bg-red-100 text-red-700 border-red-200";

    return (
      <span
        className={`text-xs font-medium px-2 py-1 rounded-full border ${badgeStyle}`}
      >
        {value} jour(s)
      </span>
    );
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between px-4">
        <div className="text-left">
          <h2 className="text-lg font-semibold text-black">Soldes de congés</h2>
          <p className="text-xs text-muted-foreground">
            Synthèse des congés disponibles par employé.
          </p>
        </div>
        <Button
          size="sm"
          variant="outline"
          className="text-primary hover:text-primary"
          onClick={fetchSoldes}
        >
          <RefreshCw className="h-4 w-4 mr-2" />
          Actualiser
        </Button>
      </div>

      <div className="px-4">
        <DynamicTable3
          columns={[
            { key: "nomComplet", label: "Employé" },
            { 
              key: "joursPris", 
              label: "Jours pris",
              render: (value: number) => `${value} jour(s)`
            },
            { 
              key: "solde", 
              label: "Solde disponible",
              render: renderSoldeBadge
            },
            { 
              key: "dateReference", 
              label: "Date référence",
              render: (value: string) => new Date(value).toLocaleDateString()
            },
          ]}
          data={filteredData}
          isLoading={loading}
          onFilter={handleFilter}
          filterPlaceholder="Rechercher par nom..."
        />
      </div>
    </div>
  );
}