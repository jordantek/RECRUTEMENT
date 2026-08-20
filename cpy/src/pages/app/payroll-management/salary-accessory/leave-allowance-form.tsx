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
import { RefreshCw, Save } from "lucide-react";
import useCompanyStore from "@/contexts/CompanyContext";
import { toast } from "sonner";

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
  const { selectedCompany } = useCompanyStore();

  const [selectedEmployee, setSelectedEmployee] = useState<{
    contratId: number;
    employeId: number;
    nom: string;
    prenom: string;
    matricule?: string;
  } | null>(null);

  const [mois, setMois] = useState("");
  const [typeOperation, setTypeOperation] = useState<OperationType>("ALLOCATION");
  const [result, setResult] = useState<ApiResponse | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const calculateAllowance = async () => {
    if (!selectedEmployee?.contratId || !mois) return;
    
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
      console.error("Erreur calcul allocation:", err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleSave = async () => {
    if (!result || !selectedCompany?.id || !selectedEmployee) {
      toast.error("Données incomplètes pour la sauvegarde");
      return;
    }

    try {
      setIsSaving(true);
      
      const payload = {
        idContratEmploye: selectedEmployee.contratId,
        companyId: selectedCompany.id,
        resultatCongeDTO: {
          ...result,
          employe: {
            matricule: selectedEmployee.matricule || "",
            nom: selectedEmployee.nom,
            prenom: selectedEmployee.prenom
          }
        }
      };

      await apiService.post(
        {
          url: apiRoutes.admin.app.leaveAllowance.save,
          body: payload
        },
        {
          userToken: `${user?.type} ${user?.token}`,
          hasNoSuccessModal: false

        }
      );

      onCancel();
    } catch (error) {
      console.error("Erreur sauvegarde allocation:", error);
    } finally {
      setIsSaving(false);
    }
  };

  useEffect(() => {
    const timer = setTimeout(calculateAllowance, 500);
    return () => clearTimeout(timer);
  }, [selectedEmployee, mois, typeOperation]);

  const tableData = result?.bulletinsUtilises?.map((b, idx) => ({
    id: idx,
    mois: b.mois,
    tempsTravail: b.tempsTravail,
    salaireBrut: b.salaireBrut,
  })) || [];

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between px-4">
        <div className="text-left">
          <h2 className="text-lg font-semibold text-black">Calcul d'allocation de congés</h2>
          <p className="text-xs text-muted-foreground">
            Calcul et enregistrement des allocations de congés
          </p>
        </div>
        <Button
          variant="outline"
          className="text-primary hover:text-primary"
          onClick={onCancel}
        >
          Retour
        </Button>
      </div>

      {/* Formulaire de sélection */}
      <div className="px-4">
        <div className="bg-muted rounded-lg border p-4 space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="space-y-2">
              <Label>Agent</Label>
              <Select
                onValueChange={(value) => {
                  const employee = employees.find((e) => e.employeId.toString() === value);
                  setSelectedEmployee(employee ? {
                    contratId: employee.contratEmployeId || 0,
                    employeId: employee.employeId,
                    nom: employee.nom,
                    prenom: employee.prenom,
                    matricule: employee.matricule
                  } : null);
                }}
                value={selectedEmployee?.employeId.toString() || ""}
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

            <div className="space-y-2">
              <Label>Mois concerné</Label>
              <Input
                type="month"
                className="bg-white"
                value={mois}
                onChange={(e) => setMois(e.target.value)}
                disabled={isLoading}
              />
            </div>

            <div className="space-y-2">
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
                  <SelectItem value="ALLOCATION">Allocation</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>

          <div className="flex justify-end">
            <Button 
              onClick={calculateAllowance}
              disabled={!selectedEmployee || !mois || isLoading}
            >
              <RefreshCw className={`h-4 w-4 mr-2 ${isLoading ? "animate-spin" : ""}`} />
              {isLoading ? "Calcul en cours..." : "Calculer"}
            </Button>
          </div>
        </div>
      </div>

      {/* Résultats */}
      {isLoading ? (
        <div className="flex justify-center items-center py-8">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
        </div>
      ) : error ? (
        <div className="px-4">
          <div className="p-4 border rounded bg-red-50 text-red-600 text-sm">
            {error}
          </div>
        </div>
      ) : result ? (
        <div className="px-4 space-y-4">
          {/* Informations employé */}
          <div className="bg-white rounded-lg border p-4">
            <h3 className="font-medium">
              {result.employe.prenom} {result.employe.nom} ({result.employe.matricule})
            </h3>
            <p className="text-sm text-muted-foreground">
              {typeOperation === 'ALLOCATION' ? 'Allocation' : 'Indemnité de congé'} - {mois}
            </p>
          </div>

          {/* Tableau des bulletins */}
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
                render: (val) => `${val?.toLocaleString('fr-FR')} FCFA` || "—",
              },
            ]}
            data={tableData}
            isLoading={false}
          />

          {/* Résumé */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="bg-white rounded-lg border p-4">
              <p className="text-sm text-muted-foreground">Salaire journalier normal</p>
              <p className="font-medium">{result.salaireJournalierNormal?.toLocaleString('fr-FR')} FCFA</p>
            </div>
            <div className="bg-white rounded-lg border p-4">
              <p className="text-sm text-muted-foreground">Nombre de jours</p>
              <p className="font-medium">{result.nbJours}</p>
            </div>
            <div className="bg-white rounded-lg border p-4">
              <p className="text-sm text-muted-foreground">Montant total</p>
              <p className="font-medium text-primary">{result.montantTotal?.toLocaleString('fr-FR')} FCFA</p>
            </div>
          </div>

          {/* Bouton de confirmation */}
          <div className="flex justify-end pt-4">
            <Button 
              onClick={handleSave}
              disabled={isSaving}
              className="bg-green-600 hover:bg-green-700 mb-4"
            >
              <Save className="h-4 w-4 mr-2" />
              {isSaving ? "Enregistrement en cours..." : "Confirmer et enregistrer"}
            </Button>
          </div>
        </div>
      ) : (
        <div className="px-4">
          <div className="p-4 border rounded bg-muted text-muted-foreground text-sm text-center">
            Sélectionnez un agent, un mois et un type d'opération pour afficher les résultats.
          </div>
        </div>
      )}
    </div>
  );
}