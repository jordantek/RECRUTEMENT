import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { ClockIcon, UserPlusIcon } from "lucide-react";
import StatCard from "@/components/useful/StatCard";
import { DynamicTable3 } from "@/components/tables/dynamic-table-3";
import { EmployeeSelect } from "@/components/useful/EmployeeSelect";
import { WorkingTimeForm } from "./working-time-form";
import apiService from "@/api/apiService";
import apiRoutes from "@/api/apiRoutes";
import { useAuth } from "@/lib/auth";
import useCompanyStore from "@/contexts/CompanyContext";

type WorkingTimeItem = {
  id: number;
  nom_agent: string;
  mois: string;
  jours: number;
  departement: string;
  departementId: number | null;
};

export default function WorkingTimeTab() {
  const { selectedCompany } = useCompanyStore();
  const { user } = useAuth();

  const [isFormVisible, setIsFormVisible] = useState(false);
  const [workingTimes, setWorkingTimes] = useState<WorkingTimeItem[]>([]);
  const [filteredData, setFilteredData] = useState<WorkingTimeItem[]>([]);
  const [selectedDepartment, setSelectedDepartment] = useState<string>("");
  const [departements, setDepartements] = useState<{ id: number; libelle: string }[]>([]);

  useEffect(() => {
    const fetchData = async () => {
      if (!selectedCompany?.id) return;

      try {
        const response = await apiService.get(
          {
            url: apiRoutes.admin.app.workingTime.listByCompany(selectedCompany.id),
          },
          {
            userToken: `${user?.type} ${user?.token}`,
            hasNoSuccessModal: true,
          }
        );

        const rawData = response.data || [];

        const mapped: WorkingTimeItem[] = rawData.map((item: any) => ({
          id: item.id,
          nom_agent: `${item.employe?.prenom || ""} ${item.employe?.nom || ""}`,
          mois: item.mois,
          jours: item.nombreJour,
          departement: item.contratEmploye?.departementDTO?.libelle || "Non défini",
          departementId: item.contratEmploye?.departementDTO?.id || null,
        }));

        setWorkingTimes(mapped);
        setFilteredData(mapped);
      } catch (error) {
        console.error("Erreur lors du chargement des temps de travail :", error);
      }
    };

    fetchData();
  }, [selectedCompany?.id, isFormVisible]);

  useEffect(() => {
    const fetchDepartements = async () => {
      if (!selectedCompany?.id) return;

      try {
        const response = await apiService.get(
          {
            url: apiRoutes.admin.app.company.departement.list_byCompany,
            params: { companyId: selectedCompany.id },
          },
          {
            userToken: `${user?.type} ${user?.token}`,
            hasNoSuccessModal: true,
          }
        );
        setDepartements(response.data || []);
      } catch (error) {
        console.error("Erreur chargement des départements", error);
      }
    };

    fetchDepartements();
  }, [selectedCompany?.id]);

  // 🔁 Mise à jour automatique des données filtrées
  useEffect(() => {
    let result = workingTimes;

    if (selectedDepartment) {
      result = result.filter(
        (item) => item.departementId?.toString() === selectedDepartment
      );
    }

    setFilteredData(result);
  }, [selectedDepartment, workingTimes]);


  const handleDepartmentChange = (departmentId: string) => {
    setSelectedDepartment(departmentId);
  };

  return (
    <div className="space-y-4 px-4">
      {!isFormVisible ? (
        <>
          {/* Header */}
          <div className="flex items-center justify-between">
            <div className="text-left">
              <h2 className="text-lg font-semibold text-black">Nombre de jours de travail</h2>
              <p className="text-xs text-muted-foreground">
                Liste des jours de travail déclarés par agent pour chaque mois.
              </p>
            </div>
            <div className="flex items-center gap-2">
              <EmployeeSelect />
              <Button
                size="sm"
                className="flex items-center gap-2 rounded-md bg-primary text-primary-foreground hover:bg-primary/80"
                onClick={() => setIsFormVisible(true)}
              >
                <UserPlusIcon className="h-4 w-4 mr-2" />
                Enregistrement temps de travail
              </Button>
            </div>
          </div>

          {/* Statistiques */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <StatCard
              icon={ClockIcon}
              title="Total lignes"
              value={filteredData.length}
              bgColor="bg-blue-100"
              textColor="text-blue-600"
            />
            <StatCard
              icon={ClockIcon}
              title="Total jours travaillés"
              value={filteredData.reduce((acc, curr) => acc + curr.jours, 0)}
              bgColor="bg-green-100"
              textColor="text-green-600"
            />
            <StatCard
              icon={ClockIcon}
              title="Moyenne par agent"
              value={
                filteredData.length
                  ? (filteredData.reduce((acc, curr) => acc + curr.jours, 0) / filteredData.length).toFixed(1)
                  : "0"
              }
              bgColor="bg-yellow-100"
              textColor="text-yellow-600"
            />
          </div>

          {/* Filtres et tableau */}
          <div>
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center gap-4">
                <select
                  value={selectedDepartment}
                  onChange={(e) => handleDepartmentChange(e.target.value)}
                  className="rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2"
                >
                  <option value="">Tous les départements</option>
                  {departements.map((dept) => (
                    <option key={dept.id} value={dept.id.toString()}>
                      {dept.libelle}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <DynamicTable3
              columns={[
                {
                  key: "nom_agent",
                  label: "Agent",
                  render: (value) => (
                    <div className="flex items-center gap-2">
                      <p className="w-8 h-8 flex items-center justify-center rounded-full border text-sm font-medium bg-muted text-muted-foreground">
                        {value?.toUpperCase().charAt(0)}
                      </p>
                      <span className="font-medium">{value}</span>
                    </div>
                  ),
                },
                { key: "mois", label: "Mois" },
                {
                  key: "jours",
                  label: "Jours de travail",
                  render: (val) => `${val} j`,
                },
                {
                  key: "departement",
                  label: "Département",
                },
              ]}
              data={filteredData}
              onAdd={() => {}}
              onEdit={() => {}}
              onDelete={() => {}}
            />
          </div>
        </>
      ) : (
        <WorkingTimeForm onCancel={() => setIsFormVisible(false)} />
      )}
    </div>
  );
}
