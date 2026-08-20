import { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { DynamicTable3 } from "@/components/tables/dynamic-table-3";
import { RefreshCw } from "lucide-react";
import apiService from "@/api/apiService";
import apiRoutes from "@/api/apiRoutes";
import { useAuth } from "@/lib/auth";
import useCompanyStore from "@/contexts/CompanyContext";

interface FixedSalary {
  id: number;
  libelle: string;
}

export default function FixedSalaryTab() {
  const { user } = useAuth();
  const { selectedCompany } = useCompanyStore();
  const [data, setData] = useState<FixedSalary[]>([]);
  const [filteredData, setFilteredData] = useState<FixedSalary[]>([]);
  const [loading, setLoading] = useState(false);

  const fetchFixedSalaries = async () => {
    if (!selectedCompany?.id) return;

    setLoading(true);
    try {
      const response = await apiService.get(
        {
          url: apiRoutes.admin.app.rubriquesFixes.listByCompany(selectedCompany.id),
        },
        {
          userToken: `${user?.type} ${user?.token}`,
          hasNoSuccessModal: true,
        }
      );

      const result = response.data || [];
      setData(result);
      setFilteredData(result);
    } catch (error) {
      console.error("Erreur lors du chargement des éléments fixes:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchFixedSalaries();
  }, [selectedCompany?.id]);

  const handleFilter = (query: string) => {
    const lower = query.trim().toLowerCase();
    if (!lower) return setFilteredData(data);
    setFilteredData(
      data.filter((item) => item.libelle.toLowerCase().includes(lower))
    );
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between px-4">
        <div className="text-left">
          <h2 className="text-lg font-semibold text-black">Éléments fixes du salaire</h2>
          <p className="text-xs text-muted-foreground">
            Liste des composantes fixes de rémunération
          </p>
        </div>
        <Button
          size="sm"
          variant="outline"
          className="text-primary hover:text-primary"
          onClick={fetchFixedSalaries}
        >
          <RefreshCw className="h-4 w-4 mr-2" />
          Actualiser
        </Button>
      </div>

      <div className="px-4">
        <DynamicTable3
          columns={[
            { 
              key: "id", 
              label: "ID",
              className: "text-muted-foreground"
            },
            { 
              key: "libelle", 
              label: "Libellé",
              className: "font-normal"
            }
          ]}
          data={filteredData}
          isLoading={loading}
          onFilter={handleFilter}
          filterPlaceholder="Rechercher par libellé..."
        />
      </div>
    </div>
  );
}