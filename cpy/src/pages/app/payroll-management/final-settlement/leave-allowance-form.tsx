import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectTrigger,
  SelectValue,
  SelectContent,
  SelectItem
} from "@/components/ui/select";
import { Label } from "@/components/ui/label";
import apiService from "@/api/apiService";
import apiRoutes from "@/api/apiRoutes";
import { useAuth } from "@/lib/auth";
import useEmployeeStore from "@/contexts/useEmployeeStore";
import { EmployeeActifType } from "@/types/employee/EmployeeType";
import { DynamicTable3 } from "@/components/tables/dynamic-table-3";

type OperationType = "ALLOCATION" | "INDEMNITE_CONGE";

type Bulletin = {
  mois: string;
  tempsTravail: number;
  salaireBrut: number;
};

type ApiResponse = {
  totalTemps: number;
  totalSalaire: number;
  salaireJournalierProvisoire: number;
  salaireJournalierNormal: number;
  nbJours: number;
  montantTotal: number;
  typeOperation: OperationType;
  bulletinsUtilises: Bulletin[];
  employe: {
    matricule: string;
    nom: string;
    prenom: string;
  };
};

type Props = {
  onCancel: () => void;
};

export function LeaveAllowanceForm({ onCancel }: Props) {
  const { user } = useAuth();
  const { employees } = useEmployeeStore();

  const [selectedEmployee, setSelectedEmployee] = useState<{
    contratId: number;
    employeId: number;
    nom: string;
    prenom: string;
    matricule?: string;
  } | null>(null);

  const [mois, setMois] = useState("");
  const [typeOperation, setTypeOperation] = useState<OperationType>();
  const [result, setResult] = useState<ApiResponse | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!selectedEmployee?.contratId || !mois || !typeOperation) {
      setResult(null);
      return;
    }

    const calculateAllowance = async () => {
      try {
        setIsLoading(true);
        setError(null);

        const response = await apiService.post(
          {
            url: apiRoutes.admin.app.leaveAllowance.calculate,
            body: {
              idContratEmploye: selectedEmployee.contratId,
              typeOperation,
              mois
            }
          },
          {
            userToken: `${user?.type} ${user?.token}`,
            hasNoSuccessModal: true
          }
        );

        setResult(response.data);
      } catch (err) {
        setError("Erreur lors du calcul. Veuillez vérifier les données.");
      } finally {
        setIsLoading(false);
      }
    };

    const timer = setTimeout(calculateAllowance, 500);
    return () => clearTimeout(timer);
  }, [selectedEmployee, mois, typeOperation, user]);

  const tableData = result?.bulletinsUtilises.map((b, idx) => ({
    id: idx,
    mois: b.mois,
    tempsTravail: b.tempsTravail,
    salaireBrut: b.salaireBrut,
  })) ?? [];

  return (
    <div className="space-y-4 px-4">
      <div className="flex items-center justify-between">
        <h2 className="text-lg font-semibold text-black">Calcul d'allocation de congés</h2>
        <Button variant="outline" onClick={onCancel}>
          Retour
        </Button>
      </div>

      {/* Formulaire de sélection */}
      <div className="border rounded-md p-4 bg-muted space-y-1 shadow-md">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="space-y-1.5">
            <Label>Agent</Label>
            <Select
              onValueChange={(value) => {
                const employee = employees.find((e) => e.employeId.toString() === value);
                if (employee) {
                  setSelectedEmployee({
                    contratId: employee.contratEmployeId !== undefined ? employee.contratEmployeId : 0,
                    employeId: employee.employeId,
                    nom: employee.nom,
                    prenom: employee.prenom,
                    matricule: employee.matricule
                  });
                } else {
                  setSelectedEmployee(null);
                }
              }}
              disabled={isLoading}
              value={selectedEmployee?.employeId.toString() ?? ""}
            >
              <SelectTrigger className="bg-white">
                <SelectValue placeholder="Sélectionner un employé" />
              </SelectTrigger>
              <SelectContent>
                {employees.map((e: EmployeeActifType) => (
                  <SelectItem key={e.employeId} value={e.employeId.toString()}>
                    {e.nom} {e.prenom}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
          <div className="space-y-1.5">
            <Label>Mois concerné</Label>
            <Input
              type="month"
              className="bg-white"
              value={mois}
              onChange={(e) => setMois(e.target.value)}
              disabled={isLoading}
            />
          </div>
          <div className="space-y-1.5">
            <Label>Type d'opération</Label>
            <Select
              onValueChange={(value: OperationType) => setTypeOperation(value)}
              value={typeOperation}
              disabled={isLoading}
            >
              <SelectTrigger className="bg-white">
                <SelectValue placeholder="Choisir un type" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="INDEMNITE_CONGE">Indemnités de congés</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>
      </div>

      {/* Résultats ou états */}
      {isLoading ? (
        <div className="flex justify-center items-center py-8">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
        </div>
      ) : error ? (
        <div className="p-4 border rounded bg-red-50 text-red-600 text-sm text-center">
          {error}
        </div>
      ) : result ? (
        <>
          {/* Informations employé */}
          <div className=" rounded-md p-1 bg-white">
            <h3 className="font-medium">
              {result.employe.prenom} {result.employe.nom} ({result.employe.matricule})
            </h3>
            <p className="text-sm text-muted-foreground">
              {typeOperation === 'ALLOCATION' ? 'Allocation' : 'Indemnité de congé'} - {mois}
            </p>
          </div>

          <DynamicTable3
            columns={[
              { key: "mois", label: "Mois" },
              {
                key: "tempsTravail",
                label: "Temps de travail",
                render: (val) => `${val}h`,
              },
              {
                key: "salaireBrut",
                label: "Salaire brut",
                render: (val) =>
                  typeof val === "number"
                    ? `${val.toLocaleString()} FCFA`
                    : "—",
              },
            ]}
            data={tableData}
            isLoading={false}
            onAdd={() => {}}
          />

          {/* Résumé */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 ">
            <div className="border rounded-md p-4">
              <p className="text-sm text-muted-foreground">Salaire journalier normal</p>
              <p className="font-medium">{result.salaireJournalierNormal.toLocaleString()} FCFA</p>
            </div>
            <div className="border rounded-md p-4">
              <p className="text-sm text-muted-foreground">Nombre de jours</p>
              <p className="font-medium">{result.nbJours}</p>
            </div>
            <div className="border rounded-md p-4">
              <p className="text-sm text-muted-foreground">Montant total</p>
              <p className="font-medium text-primary">{result.montantTotal.toLocaleString()} FCFA</p>
            </div>
          </div>
        </>
      ) : (
        <div className="p-4 border rounded bg-muted text-muted-foreground text-sm text-center">
          Sélectionnez un agent, un mois et un type d'opération pour afficher les résultats.
        </div>
      )}
    </div>
  );
}
