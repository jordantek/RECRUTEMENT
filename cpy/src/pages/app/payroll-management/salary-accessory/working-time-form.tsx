import { useEffect, useState, useMemo } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectTrigger,
  SelectValue,
  SelectContent,
  SelectItem,
} from "@/components/ui/select";
import { Label } from "@/components/ui/label";
import Lottie from "lottie-react";
import successAnimation from "@/lottiesfiles/sucees.json";

import apiService from "@/api/apiService";
import apiRoutes from "@/api/apiRoutes";
import { useAuth } from "@/lib/auth";
import useCompanyStore from "@/contexts/CompanyContext";

type Employee = {
  id: number;
  nom: string;
  departement: string;
  departementId: number | null;
};

type Props = {
  onCancel: () => void;
};

export function WorkingTimeForm({ onCancel }: Props) {
  const { selectedCompany } = useCompanyStore();
  const { user } = useAuth();

  const [mois, setMois] = useState("");
  const [departement, setDepartement] = useState("all");
  const [departements, setDepartements] = useState<{ id: number; libelle: string }[]>([]);
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [tempsTravail, setTempsTravail] = useState<{ [id: number]: number }>({});
  const [currentPage, setCurrentPage] = useState(1);
  const [successSubmit, setSuccessSubmit] = useState(false);
  const pageSize = 5;

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
        const data = response.data || [];
        setDepartements(data);
      } catch (error) {
        console.error("Erreur chargement des départements", error);
      }
    };

    fetchDepartements();
  }, [selectedCompany?.id]);

  useEffect(() => {
    const fetchWorkingTimes = async () => {
      if (!mois || !selectedCompany?.id) {
        setEmployees([]);
        setTempsTravail({});
        return;
      }

      try {
        const response = await apiService.get(
          {
            url: apiRoutes.admin.app.workingTime.listByCompanyByMonth(selectedCompany.id, mois),
          },
          {
            userToken: `${user?.type} ${user?.token}`,
            hasNoSuccessModal: true,
          }
        );

        const data = response.data || [];

        if (data.length === 0) {
          setEmployees([]);
          setTempsTravail({});
          return;
        }

        const mappedEmployees = data.map((item: any) => ({
          id: item.employe.id,
          nom: `${item.employe.prenom} ${item.employe.nom}`,
          departement: item.contratEmploye?.departement?.libelle || "Non défini",
          departementId: item.contratEmploye?.departement?.id || null,
        }));

        const mappedTemps = data.reduce(
          (acc: { [key: number]: number }, item: any) => {
            acc[item.employe.id] = item.nombreJour;
            return acc;
          },
          {}
        );

        setEmployees(mappedEmployees);
        setTempsTravail(mappedTemps);
        setCurrentPage(1);
      } catch (error) {
        console.error("Erreur chargement temps de travail :", error);
        setEmployees([]);
        setTempsTravail({});
      }
    };

    fetchWorkingTimes();
  }, [mois, selectedCompany?.id]);

  const filteredEmployees = useMemo(() => {
    if (departement === "all") {
      return employees;
    }
    return employees.filter(emp => 
      emp.departementId?.toString() === departement
    );
  }, [employees, departement]);

  const paginatedEmployees = filteredEmployees.slice(
    (currentPage - 1) * pageSize,
    currentPage * pageSize
  );
  const totalPages = Math.ceil(filteredEmployees.length / pageSize);

  const handleChangeTemps = (id: number, value: string) => {
    const temps = parseFloat(value);
    setTempsTravail((prev) => ({ ...prev, [id]: isNaN(temps) ? 0 : temps }));
  };

  const handleSubmit = async () => {
    if (!selectedCompany?.id || !mois) return;

    const payload = filteredEmployees.map((emp) => ({
      employeId: emp.id,
      nombreJour: tempsTravail[emp.id] || 0,
    }));

    try {
      await apiService.post(
        {
          url: apiRoutes.admin.app.workingTime.saveByCompany(selectedCompany.id, mois),
          body: payload,
        },
        {
          userToken: `${user?.type} ${user?.token}`,
          hasNoSuccessModal: true,
        }
      );
      setSuccessSubmit(true);
    } catch (error) {
      console.error("Erreur lors de l'enregistrement des temps de travail :", error);
    }
  };

  return (
    <>
      <div className="space-y-3 px-4">
        <h2 className="text-lg font-semibold text-black">
          Enregistrement du nombre de jours ou heures de travail
        </h2>

        {/* Bloc Filtres */}
        <div className="border rounded-md p-4 bg-muted space-y-1 shadow-md">
          <h3 className="text-sm font-medium text-muted-foreground">Filtres de déclaration</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="space-y-1.5">
              <Label htmlFor="mois">Mois concerné</Label>
              <Input
                id="mois"
                type="month"
                className="bg-white"
                value={mois}
                onChange={(e) => setMois(e.target.value)}
              />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="departement">Département</Label>
              <Select onValueChange={setDepartement} value={departement}>
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
          </div>
        </div>

        {/* Tableau employés */}
        {mois && (
          <>
            {filteredEmployees.length > 0 ? (
              <div className="border rounded-md overflow-x-auto">
                <table className="min-w-full text-sm">
                  <thead className="bg-muted text-muted-foreground">
                    <tr>
                      <th className="px-4 py-2 text-left">#</th>
                      <th className="px-4 py-2 text-left">Nom</th>
                      <th className="px-4 py-2 text-left">Département</th>
                      <th className="px-4 py-2 text-left">Temps de travail (heures/jour)</th>
                    </tr>
                  </thead>
                  <tbody>
                    {paginatedEmployees.map((emp, idx) => (
                      <tr key={emp.id} className="border-b">
                        <td className="px-4 py-2">{(currentPage - 1) * pageSize + idx + 1}</td>
                        <td className="px-4 py-2">{emp.nom}</td>
                        <td className="px-4 py-2">{emp.departement}</td>
                        <td className="px-4 py-2">
                          <Input
                            type="number"
                            step="0.1"
                            value={tempsTravail[emp.id]}
                            onChange={(e) => handleChangeTemps(emp.id, e.target.value)}
                            className="w-32"
                          />
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>

                <div className="flex justify-between items-center px-4 py-2">
                  <p className="text-sm text-muted-foreground">
                    Page {currentPage} sur {totalPages}
                  </p>
                  <div className="space-x-2">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => setCurrentPage((p) => Math.max(p - 1, 1))}
                      disabled={currentPage === 1}
                    >
                      Précédent
                    </Button>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => setCurrentPage((p) => Math.min(p + 1, totalPages))}
                      disabled={currentPage === totalPages}
                    >
                      Suivant
                    </Button>
                  </div>
                </div>
              </div>
            ) : (
              <div className="p-4 border rounded bg-muted text-muted-foreground text-sm text-center">
                Aucun temps de travail trouvé {departement !== "all" ? "pour ce département" : ""} pour le mois sélectionné.
              </div>
            )}
          </>
        )}

        {/* Boutons */}
        {filteredEmployees.length > 0 && (
          <div className="flex justify-end gap-4 pt-4">
            <Button
              variant="outline"
              onClick={onCancel}
              className="border border-red-500 text-red-500 hover:bg-red-500 hover:text-white"
            >
              Annuler
            </Button>
            <Button onClick={handleSubmit}>
              Enregistrer
            </Button>
          </div>
        )}
      </div>

      {/* ✅ Modale de succès */} 
      {successSubmit && (
        <div className="fixed inset-0 z-50 bg-black/40 backdrop-blur-sm flex items-center justify-center px-4">
          <div className="w-full max-w-[300px] bg-white dark:bg-zinc-900 rounded-2xl shadow-2xl p-6 text-center space-y-6 animate-fade-in">
            <div className="w-35 h-25 mx-auto">
              <Lottie animationData={successAnimation} loop={false} />
            </div>
            <h2 className="text-base font-semibold text-zinc-800 dark:text-zinc-100">
              Temps de travail enregistrés avec succès !
            </h2>
            <Button variant={"outline"} onClick={() => setSuccessSubmit(false)}>
              OK
            </Button>
          </div>
        </div>
      )}
    </>
  );
}