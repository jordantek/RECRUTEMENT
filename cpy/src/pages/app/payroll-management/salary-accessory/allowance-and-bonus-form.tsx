import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Select, SelectTrigger, SelectValue, SelectContent, SelectItem } from "@/components/ui/select";
import { Label } from "@/components/ui/label";
import apiService from "@/api/apiService";
import apiRoutes from "@/api/apiRoutes";
import { useAuth } from "@/lib/auth";
import useCompanyStore from "@/contexts/CompanyContext";

type Employee = {
  id: number;
  nom: string;
  montantExist?: number;
};

type Department = {
  id: number;
  libelle: string;
};

type AllowanceType = {
  id: number;
  libelle: string;
  nature: string;
};

type Props = {
  onCancel: () => void;
};

export function AllowanceAndBonusForm({ onCancel }: Props) {
  const { selectedCompany } = useCompanyStore();
  const { user } = useAuth();

  const [mois, setMois] = useState("");
  const [departement, setDepartement] = useState("all");
  const [prime, setPrime] = useState("");
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [montants, setMontants] = useState<{ [id: number]: number }>({});
  const [departements, setDepartements] = useState<Department[]>([]);
  const [allowanceTypes, setAllowanceTypes] = useState<AllowanceType[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  // Pagination states
  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 5;

  useEffect(() => {
    const fetchInitialData = async () => {
      try {
        setIsLoading(true);
        
        // Récupération des départements
        const deptResponse = await apiService.get(
          {
            url: apiRoutes.admin.app.company.departement.list_byCompany,
            params: { companyId: selectedCompany?.id }
          },
          {
            userToken: `${user?.type} ${user?.token}`,
            hasNoSuccessModal: true
          }
        );
        setDepartements(deptResponse.data || []);

        // Récupération des types de primes
        const allowanceResponse = await apiService.get(
          {
            url: apiRoutes.admin.app.contrat.rubriques.variableList,
          },
          {
            userToken: `${user?.type} ${user?.token}`,
            hasNoSuccessModal: true
          }
        );
        setAllowanceTypes(allowanceResponse.data || []);
      } catch (error) {
        console.error("Erreur chargement des données initiales", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchInitialData();
  }, [selectedCompany?.id]);

  useEffect(() => {
    const fetchEmployeesWithAllowances = async () => {
      if (!mois || !prime || !selectedCompany?.id) {
        setEmployees([]);
        setMontants({});
        return;
      }

      try {
        setIsLoading(true);
        const payload = {
          companyId: selectedCompany.id,
          rubriqueId: parseInt(prime),
          mois,
          departementId: departement === "all" ? null : parseInt(departement)
        };

        const response = await apiService.post(
          {
            url: apiRoutes.admin.app.allowanceAndBonus.listByCompany,
            body: payload
          },
          {
            userToken: `${user?.type} ${user?.token}`,
            hasNoSuccessModal: true
          }
        );

        // Vérification que response.data existe et est un tableau
        if (!response?.data || !Array.isArray(response.data)) {
          setEmployees([]);
          setMontants({});
          return;
        }

        const employeesData = response.data.map((item: any) => {
          const employe = item.employe || {};
          return {
            id: employe.id,
            nom: `${employe.prenom || ''} ${employe.nom || ''}`.trim(),
            montantExist: item.montantRubrique || 0
          };
        });

        setEmployees(employeesData);
        
        // Initialisation des montants
        const initialMontants = employeesData.reduce((acc: { [key: number]: number }, emp: { id: number; montantExist: number }) => {
          acc[emp.id] = emp.montantExist;
          return acc;
        }, {});
        
        setMontants(initialMontants);
        setCurrentPage(1);
      } catch (error) {
        console.error("Erreur chargement des employés avec primes", error);
        setEmployees([]);
        setMontants({});
      } finally {
        setIsLoading(false);
      }
    };

    fetchEmployeesWithAllowances();
  }, [mois, prime, departement, selectedCompany?.id]);

  const handleChangeMontant = (id: number, value: string) => {
    const montant = parseFloat(value);
    setMontants((prev) => ({ ...prev, [id]: isNaN(montant) ? 0 : montant }));
  };

  const handleSubmit = async () => {
    if (!prime || !mois || !selectedCompany?.id) {
      alert("Veuillez sélectionner un type de prime, un mois et une entreprise");
      return;
    }
  
    try {
      setIsLoading(true);
      
      // Préparation du payload exactement comme l'API l'attend
      const payload = employees
        .filter(emp => montants[emp.id] > 0)
        .map(emp => ({
          montantRubrique: montants[emp.id],
          moisRubrique: mois,
          employe: { id: emp.id },
          rubriqueId: parseInt(prime)
        }));
       
      await apiService.post(
        {
          url: apiRoutes.admin.app.allowanceAndBonus.saveByCompany(selectedCompany.id),
          body: payload
        },
        {
          userToken: `${user?.type} ${user?.token}`,
          hasNoSuccessModal: false
        }
      );
      
    } catch (error) {
      console.error("Erreur lors de l'enregistrement", error);
    } finally {
      setIsLoading(false);
    }
  };

  const paginatedEmployees = employees.slice(
    (currentPage - 1) * pageSize,
    currentPage * pageSize
  );

  const totalPages = Math.ceil(employees.length / pageSize);

  return (
    <div className="space-y-6 px-4">
      <h2 className="text-lg font-semibold text-black">Ajouter des indemnités et primes</h2>

      {/* Bloc Filtres */}
      <div className="border rounded-md p-4 bg-muted space-y-1 shadow-md">
        <h3 className="text-sm font-medium text-muted-foreground">Filtres de déclaration</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="space-y-1.5">
            <Label htmlFor="mois">Mois concerné</Label>
            <Input
              id="mois"
              type="month"
              className="bg-white"
              value={mois}
              onChange={(e) => setMois(e.target.value)}
              disabled={isLoading}
            />
          </div>
          <div className="space-y-1.5">
            <Label htmlFor="departement">Département</Label>
            <Select 
              onValueChange={setDepartement} 
              value={departement}
              disabled={isLoading}
            >
              <SelectTrigger id="departement" className="bg-white">
                <SelectValue placeholder="Choisir un département" />
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
          <div className="space-y-1.5">
            <Label htmlFor="prime">Type de prime</Label>
            <Select 
              onValueChange={setPrime} 
              value={prime}
              disabled={isLoading}
            >
              <SelectTrigger id="prime" className="bg-white">
                <SelectValue placeholder="Choisir une prime" />
              </SelectTrigger>
              <SelectContent>
                {allowanceTypes
                    .filter(type => type.nature === "AVANTAGE" || type.nature !== "AVANTAGE")
                    .map((type) => (
                        <SelectItem key={type.id} value={type.id.toString()}>
                            {type.libelle}
                        </SelectItem>
                    ))}
              </SelectContent>
            </Select>
          </div>
        </div>
      </div>

      {/* Tableau employés */}
      {isLoading ? (
        <div className="flex justify-center items-center py-8">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
        </div>
      ) : employees.length > 0 ? (
        <div className="border rounded-md overflow-x-auto">
          <table className="min-w-full text-sm">
            <thead className="bg-muted text-muted-foreground">
              <tr>
                <th className="px-4 py-2 text-left">#</th>
                <th className="px-4 py-2 text-left">Nom</th>
                <th className="px-4 py-2 text-left">Montant</th>
              </tr>
            </thead>
            <tbody>
              {paginatedEmployees.map((emp, idx) => (
                <tr key={emp.id} className="border-b">
                  <td className="px-4 py-2">{(currentPage - 1) * pageSize + idx + 1}</td>
                  <td className="px-4 py-2">{emp.nom}</td>
                  <td className="px-4 py-2">
                    <Input
                      type="number"
                      value={montants[emp.id] ?? ''}
                      onChange={(e) => handleChangeMontant(emp.id, e.target.value)}
                      className="w-32 bg-white"
                      disabled={isLoading}
                    />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          {/* Pagination */}
          <div className="flex justify-between items-center px-4 py-2">
            <p className="text-sm text-muted-foreground">
              Page {currentPage} sur {totalPages}
            </p>
            <div className="space-x-2">
              <Button
                variant="outline"
                size="sm"
                onClick={() => setCurrentPage((p) => Math.max(p - 1, 1))}
                disabled={currentPage === 1 || isLoading}
              >
                Précédent
              </Button>
              <Button
                variant="outline"
                size="sm"
                onClick={() => setCurrentPage((p) => Math.min(p + 1, totalPages))}
                disabled={currentPage === totalPages || isLoading}
              >
                Suivant
              </Button>
            </div>
          </div>
        </div>
      ) : (
        <div className="p-4 border rounded bg-muted text-muted-foreground text-sm text-center">
          {!mois || !prime 
            ? "Veuillez sélectionner un mois et un type de prime" 
            : departement === "all" 
              ? "Aucun employé trouvé - essayez de sélectionner un département spécifique" 
              : "Aucun employé trouvé pour ces critères"}
        </div>
      )}

      {/* Boutons */}
      {employees.length > 0 && (
        <div className="flex justify-end gap-4 pt-4">
          <Button
            variant="outline"
            onClick={onCancel}
            className="border border-red-500 text-red-500 hover:bg-red-500 hover:text-white"
            disabled={isLoading}
          >
            Annuler
          </Button>
          <Button 
            onClick={handleSubmit}
            disabled={isLoading || !prime || !mois}
          >
            {isLoading ? "Enregistrement..." : "Enregistrer"}
          </Button>
        </div>
      )}
    </div>
  );
}