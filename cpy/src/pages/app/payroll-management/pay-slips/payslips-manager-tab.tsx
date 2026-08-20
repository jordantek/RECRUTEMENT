import { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Download, Check, X, Mail } from "lucide-react";
import {
  Select,
  SelectTrigger,
  SelectValue,
  SelectContent,
  SelectItem,
} from "@/components/ui/select";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Checkbox } from "@/components/ui/checkbox";
import { Badge } from "@/components/ui/badge";
import { DynamicTable3 } from "@/components/tables/dynamic-table-3";
import apiService from "@/api/apiService";
import apiRoutes from "@/api/apiRoutes";
import { useAuth } from "@/lib/auth";
import useCompanyStore from "@/contexts/CompanyContext";

type Employee = {
  id: number;
  nom: string;
  prenom: string;
  matricule: string;
  departement: string;
};

type PeriodType = "month" | "range";

interface EmployeeTableData {
  id: number;
  matricule: string;
  nomComplet: string;
  departement: string;
  selected: boolean;
}

export default function PayslipManager() {
  const { user } = useAuth();
  const { selectedCompany } = useCompanyStore();
  const [periodType, setPeriodType] = useState<PeriodType>("month");
  const [month, setMonth] = useState<string>("");
  const [startDate, setStartDate] = useState<string>("");
  const [endDate, setEndDate] = useState<string>("");
  const [departement, setDepartement] = useState<string>("all");
  const [departements, setDepartements] = useState<{id: number, libelle: string}[]>([]);
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [selectedEmployees, setSelectedEmployees] = useState<number[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [isDownloading, setIsDownloading] = useState(false);

  const tableData: EmployeeTableData[] = employees.map(employee => ({
    id: employee.id,
    matricule: employee.matricule,
    nomComplet: `${employee.prenom} ${employee.nom}`,
    departement: employee.departement,
    selected: selectedEmployees.includes(employee.id)
  }));

  useEffect(() => {
    const fetchDepartements = async () => {
      if (!selectedCompany?.id) return;
      setIsLoading(true);
      
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
        console.error("Error fetching departments:", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchDepartements();
  }, [selectedCompany?.id, user?.token, user?.type]);

  useEffect(() => {
    const fetchEmployees = async () => {
      if (!selectedCompany?.id) return;
      setIsLoading(true);
      
      try {
        const params: any = { companyId: selectedCompany.id };
        if (departement !== "all") params.departementId = departement;

        const response = await apiService.get(
          {
            url: apiRoutes.admin.app.employee.list,
            params,
          },
          {
            userToken: `${user?.type} ${user?.token}`,
            hasNoSuccessModal: true,
          }
        );

        setEmployees(response.data || []);
        setSelectedEmployees([]);
      } catch (error) {
        console.error("Error fetching employees:", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchEmployees();
  }, [selectedCompany?.id, departement, user?.token, user?.type]);

  const toggleEmployeeSelection = (employeeId: number) => {
    setSelectedEmployees(prev =>
      prev.includes(employeeId)
        ? prev.filter(id => id !== employeeId)
        : [...prev, employeeId]
    );
  };

  const toggleSelectAll = () => {
    if (selectedEmployees.length === employees.length) {
      setSelectedEmployees([]);
    } else {
      setSelectedEmployees(employees.map(e => e.id));
    }
  };

  const handleDownload = async () => {
    if (!selectedCompany?.id || selectedEmployees.length === 0) {
      return;
    }

    setIsDownloading(true);
    
    try {
      const params = {
        companyId: selectedCompany.id,
        employeeIds: selectedEmployees.join(','),
        ...(periodType === "month" ? { month } : { startDate, endDate })
      };

      const response = await apiService.get(
        {
          url: apiRoutes.admin.app.payslip.downloadBatch,
          params,
        },
        {
          userToken: `${user?.type} ${user?.token}`,
          hasNoSuccessModal: true,
        }
      );

      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `bulletins-paie-${new Date().toISOString()}.zip`);
      document.body.appendChild(link);
      link.click();
      link.remove();

    } catch (error) {
      console.error("Error downloading payslips:", error);
    } finally {
      setIsDownloading(false);
    }
  };

  const handleSendByEmail = () => {
    // Placeholder pour la future fonctionnalité d'envoi par mail
    alert("Fonctionnalité d'envoi par mail bientôt disponible!");
  };

  return (
    <div className="space-y-6 p-1">
      <div className="space-y-1">
        <h1 className="text-2xl font-bold">Gestion des bulletins de paie</h1>
        <p className="text-sm text-muted-foreground">
          Téléchargez les bulletins de paie par période ou par mois
        </p>
      </div>

      <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
        <div className="space-y-4 rounded-lg border p-4">
          <h2 className="text-lg font-semibold">Période</h2>
          
          <div className="space-y-2">
            <Label>Type de période</Label>
            <Select 
              value={periodType} 
              onValueChange={(value: PeriodType) => setPeriodType(value)}
            >
              <SelectTrigger className="w-full">
                <SelectValue placeholder="Sélectionnez un type" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="month">Par mois</SelectItem>
                <SelectItem value="range">Par période</SelectItem>
              </SelectContent>
            </Select>
          </div>

          {periodType === "month" ? (
            <div className="space-y-2">
              <Label>Mois</Label>
              <Input
                type="month"
                value={month}
                onChange={(e) => setMonth(e.target.value)}
              />
            </div>
          ) : (
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label>Date de début</Label>
                <Input
                  type="date"
                  value={startDate}
                  onChange={(e) => setStartDate(e.target.value)}
                />
              </div>
              <div className="space-y-2">
                <Label>Date de fin</Label>
                <Input
                  type="date"
                  value={endDate}
                  onChange={(e) => setEndDate(e.target.value)}
                />
              </div>
            </div>
          )}
        </div>

        <div className="space-y-4 rounded-lg border p-4">
          <h2 className="text-lg font-semibold">Filtres</h2>
          
          <div className="space-y-2">
            <Label>Département</Label>
            <Select 
              value={departement} 
              onValueChange={setDepartement}
              disabled={isLoading}
            >
              <SelectTrigger className="w-full">
                <SelectValue placeholder="Sélectionnez un département" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">Tous les départements</SelectItem>
                {departements.map((dep) => (
                  <SelectItem key={dep.id} value={dep.id.toString()}>
                    {dep.libelle}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
        </div>
      </div>

      <div className="rounded-lg border">
        <div className="flex items-center justify-between p-4">
          <h2 className="text-lg font-semibold">Employés</h2>
          <div className="flex items-center space-x-4">
            <Button
              variant="ghost"
              size="sm"
              onClick={toggleSelectAll}
              disabled={employees.length === 0}
            >
              {selectedEmployees.length === employees.length && employees.length > 0 ? (
                <>
                  <X className="mr-2 h-4 w-4" />
                  Tout désélectionner
                </>
              ) : (
                <>
                  <Check className="mr-2 h-4 w-4" />
                  Tout sélectionner
                </>
              )}
            </Button>
            <div className="flex gap-2">
              <Button
                onClick={handleDownload}
                disabled={selectedEmployees.length === 0 || isDownloading}
                variant="outline"
              >
                {isDownloading ? (
                  "Téléchargement..."
                ) : (
                  <>
                    <Download className="mr-2 h-4 w-4" />
                    Télécharger ({selectedEmployees.length})
                  </>
                )}
              </Button>
              <Button
                onClick={handleSendByEmail}
                disabled={selectedEmployees.length === 0}
              >
                <Mail className="mr-2 h-4 w-4" />
                Envoyer par mail ({selectedEmployees.length})
              </Button>
            </div>
          </div>
        </div>

        <DynamicTable3<EmployeeTableData>
          columns={[
            {
              key: "selected",
              label: "",
              render: (value: boolean, row: EmployeeTableData) => (
                <Checkbox
                  checked={value}
                  onCheckedChange={() => toggleEmployeeSelection(row.id)}
                />
              ),
              className: "w-[40px]"
            },
            {
              key: "matricule",
              label: "Matricule"
            },
            {
              key: "nomComplet",
              label: "Nom complet"
            },
            {
              key: "departement",
              label: "Département",
              render: (value: string) => (
                <Badge variant="outline">{value}</Badge>
              )
            }
          ]}
          data={tableData}
          isLoading={isLoading}
          emptyText="Aucun employé trouvé"
        />
      </div>
    </div>
  );
}