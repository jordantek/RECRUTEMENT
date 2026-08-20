"use client";

import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button.tsx";
import { Input } from "@/components/ui/input.tsx";
import { Label } from "@/components/ui/label.tsx";
import { Select, SelectTrigger, SelectValue, SelectContent, SelectItem } from "@/components/ui/select.tsx";
import { MonthYearPicker } from "@/components/inputs/MonthYearPicker.tsx";
import apiService from "@/api/apiService.ts";
import useCompanyStore from "@/contexts/CompanyContext.ts";
import { useAuth } from "@/lib/auth.ts";
import InputNumberWithChevrons2 from "@/components/inputs/input-number-with-chevrons-2.tsx";
import ActionModal from "@/components/useful/action-modal.tsx";
import { ArrowLeftToLine } from "lucide-react";

type Props = {
  onCancel: () => void;
};

export type Rubrique = {
  id: number;
  libelle: string;
  contratEmployeId: number;
  rubriqueId: number;
  montant: number;
  statut: string;
  addedById: number;
  createdAt: string;
};

export type ContratEmployeDetail = {
  nom: string;
  prenom: string;
  employeId: number;
  contratEmployeId: number;
  matricule: string;
  companyId: number;
  date_debut: string;
  date_fin: string;
  rubriques: Rubrique[];
};

const overtimeConfigs = [
  { key: 'm12', label: 'Majoration 12%', rate: 1.12 },
  { key: 'm35', label: 'Majoration 35%', rate: 1.35 },
  { key: 'm50', label: 'Majoration 50%', rate: 1.50 },
  { key: 'm100', label: 'Majoration 100%', rate: 2.00 },
] as const;

type HourlyRates = {
  rate12Percent: number;
  rate35Percent: number;
  rate50Percent: number;
  rate100Percent: number;
  total: number;
};

export function OvertimeForm({ onCancel }: Props) {
  const { user, logout } = useAuth();
  const { selectedCompany } = useCompanyStore();

  const [contrats, setContrats] = useState<ContratEmployeDetail[]>([]);
  const [selectedEmployeId, setSelectedEmployeId] = useState<string | null>(null);
  const [selectedRubriques, setSelectedRubriques] = useState<number[]>([]);
  const [selectedDate, setSelectedDate] = useState<string | null>(null);
  const [overtimeHours, setOvertimeHours] = useState({
    m12: 0,
    m35: 0,
    m50: 0,
    m100: 0,
  });
  const [hourlyRates, setHourlyRates] = useState<HourlyRates | null>(null)
  const [brut, setBrut] = useState("0");
  const [totalHeures, setTotalHeures] = useState<number | null>(null);
  const [montantBrut, setMontantBrut] = useState<number >(0);
  const [loadingContrats, setLoadingContrats] = useState(false);
  const [loadingSubmit, setLoadingSubmit] = useState(false);
  const [isOpenAction, setIsOpenAction]=useState(false);


  const selectedContrat = contrats.find(
      (c) => c.contratEmployeId.toString() === selectedEmployeId
  );

  const fetchContrats = async () => {
    if (!selectedCompany?.id) return;
    try {
      setLoadingContrats(true);
      const response = await apiService.get(
          {
            url: `/api/paie/contrats-employes/actifs/${selectedCompany.id}`,
          },
          {
            userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
            hasNoSuccessModal: true,
            onTokenExpired: logout,
          }
      );
      setContrats(response.data || []);
    } catch (error) {
      if (error instanceof Error) {
        apiService.handleError(error.message, { hasNoFailureModal: true });
      }
    } finally {
      setLoadingContrats(false);
    }
  };

  const handleOvertimeChange = (
      field: keyof typeof overtimeHours,
      value: number | null
  ) => {
    setOvertimeHours((prev) => ({
      ...prev,
      [field]: value ?? 0,
    }));
  };

  const handleSubmit = async () => {
    if (!selectedContrat || !selectedDate) {
      apiService.handleError("Veuillez sélectionner un employé et un mois.", { hasNoFailureModal: false });
      return;
    }
    setLoadingSubmit(true);
    try {
      const payload = {
        heures12: overtimeHours.m12,
        heures35: overtimeHours.m35,
        heures50: overtimeHours.m50,
        heures100: overtimeHours.m100,
        totalHeures: totalHeures,
        majoration12: hourlyRates?.rate12Percent,
        majoration35: hourlyRates?.rate35Percent,
        majoration50: hourlyRates?.rate50Percent,
        majoration100: hourlyRates?.rate100Percent,
        montant:montantBrut,
        salaireBaseContrat: parseFloat(brut),
        salaireBrutContrat: parseFloat(brut),
        contratEmployeId: selectedContrat.contratEmployeId,
        employeId: selectedContrat.employeId,
        companyId: selectedCompany?.id,
        mois: selectedDate,
        observation: `Enregistrement des heures supplémentaires de l'employé ${selectedContrat.nom} ${selectedContrat.prenom} pour le mois de ${selectedDate}. les éléments prise en compte pour le calcul sont : ${selectedContrat.rubriques.map((r) => r.libelle).join(", ")} `,

      };

      await apiService.post({ url: '/api/paie/heures-supplementaires/save', body: payload }, {
        userToken: `${user?.type ?? ""} ${user?.token ?? ""}`
      });

      setSelectedDate(null)
      setSelectedEmployeId(null)
      setSelectedRubriques([])
      setBrut("0")
      setMontantBrut(0)
      setTotalHeures(null)
      setOvertimeHours({m12: 0, m35: 0, m50: 0, m100: 0})
      setHourlyRates(null)
      setIsOpenAction(false)
    } catch (error) {
      if (error instanceof Error) {
        apiService.handleError(error.message);
      }
    } finally {
      setLoadingSubmit(false);
    }
  };

  const handleCalcul = async () => {
    try {
      const payload = {
        dayHours41To48: overtimeHours.m12,
        dayHoursAbove48: overtimeHours.m35,
        dayHoursSundayAndHoliday: overtimeHours.m50,
        nightHoursSundayAndHoliday: overtimeHours.m100,
        grossSalary: parseFloat(brut)
      };

      const response = await apiService.post(
          { url: '/api/paie/heures-supplementaires/calculer', body: payload },
          {
            userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
            hasNoSuccessModal: true,
            onTokenExpired: logout
          }
      );


      // @ts-ignore
      const data:HourlyRates = response.data;

      if (data) {
        setHourlyRates(data);
        setMontantBrut(data.total);
      }
    } catch (error) {
      if (error instanceof Error) {
        apiService.handleError(error.message, { hasNoFailureModal: true });
      }
    }
  };

  useEffect(() => {
    if (selectedContrat) {
      setSelectedRubriques(selectedContrat.rubriques.map((r) => r.id));
    } else {
      setSelectedRubriques([]);
      setBrut("0");
    }
  }, [selectedContrat]);

  useEffect(() => {
    if (selectedContrat) {
      const total = selectedContrat.rubriques
          .filter((r) => selectedRubriques.includes(r.id))
          .reduce((sum, r) => sum + r.montant, 0);
      setBrut(total.toString());
    }
  }, [selectedRubriques, selectedContrat]);

  useEffect(() => {
    fetchContrats();
  }, [selectedCompany]);

  useEffect(() => {
    const total = Object.values(overtimeHours).reduce((sum, hours) => sum + hours, 0);
    setTotalHeures(total);
    setMontantBrut(0)
  }, [overtimeHours, brut]);

  return (
      <div className="space-y-6 ">
        {/* Header Section */}
        <div className="flex justify-between items-center pb-4">
          <div>
            <h2 className="text-lg font-semibold text-gray-800">Heures supplémentaires</h2>
            <p className="text-xs text-gray-500">
              Calcul et enregistrement des heures effectuées au-delà du temps légal
            </p>
          </div>
          <Button
              size={"sm"}
              onClick={onCancel}
          >
           <ArrowLeftToLine /> Retour
          </Button>
        </div>

        {/* Employee Selection Section */}
        <div className="bg-gray-50 p-4 rounded-lg">
          <h3 className="text-sm font-medium text-gray-700 mb-3">Période et employé</h3>
          <div className="flex flex-wrap items-center gap-4">
            <div className="flex-1 min-w-[200px]">
              <Label className="block mb-1 text-gray-600">Mois</Label>
              <MonthYearPicker
                  value={selectedDate}
                  onChange={setSelectedDate}
                  placeholder="Sélectionner un mois"
                  className="bg-white border-gray-300"
              />
            </div>

            <div className="flex-1 min-w-[200px]">
              <Label className="block mb-1 text-gray-600">Employé</Label>
              <Select
                  value={selectedEmployeId ?? ""}
                  onValueChange={setSelectedEmployeId}
                  disabled={loadingContrats}
              >
                <SelectTrigger className="bg-white border-gray-300">
                  <SelectValue placeholder="Choisir un employé" />
                </SelectTrigger>
                <SelectContent>
                  {contrats.map((contrat) => (
                      <SelectItem
                          key={contrat.contratEmployeId}
                          value={contrat.contratEmployeId.toString()}
                      >
                        {contrat.nom} {contrat.prenom}
                      </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
          </div>
        </div>

        {/* Salary Base Section */}
        {selectedContrat && (
            <div className="bg-gray-50 p-4 rounded-lg">
              <h3 className="text-sm font-medium text-gray-700 mb-3">Salaire brut</h3>
              <div className="grid gap-4">
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-2">
                  {selectedContrat.rubriques.map((rubrique) => (
                      <div key={rubrique.id} className="flex items-center space-x-2">
                        <input
                            type="checkbox"
                            id={`rubrique-${rubrique.id}`}
                            className="h-4 w-4 text-blue-600 rounded border-gray-300 focus:ring-blue-500"
                            checked={selectedRubriques.includes(rubrique.id)}
                            onChange={() => {
                              setSelectedRubriques((prev) =>
                                  prev.includes(rubrique.id)
                                      ? prev.filter((id) => id !== rubrique.id)
                                      : [...prev, rubrique.id]
                              );
                            }}
                        />
                        <label htmlFor={`rubrique-${rubrique.id}`} className="text-sm text-gray-700">
                          {rubrique.libelle} <span className="text-gray-500">({rubrique.montant.toLocaleString()} FCFA)</span>
                        </label>
                      </div>
                  ))}
                </div>

                <div className="flex justify-end">
                  <div className="w-full sm:w-64">
                    <Label className="block mb-1 text-gray-600">Salaire brut total</Label>
                    <div className="relative">
                      <Input
                          value={parseFloat(brut).toLocaleString()}
                          readOnly
                          className="bg-white cursor-not-allowed pr-14 text-right font-medium border-gray-300"
                      />
                      <span className="absolute right-3 top-1/2 -translate-y-1/2 text-sm text-gray-500">
                    FCFA
                  </span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
        )}

        {/* Overtime Hours Section */}
        <div className="bg-gray-50 p-4 rounded-lg">
          <h3 className="text-sm font-medium text-gray-700 mb-3">Heures supplémentaires</h3>
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            {overtimeConfigs.map((config) => (
                <div key={config.key} className="bg-white p-3 rounded border border-gray-200">
                  <InputNumberWithChevrons2
                      minValue={0}
                      label={config.label}
                      value={overtimeHours[config.key]}
                      onValueChange={(value) => handleOvertimeChange(config.key, value)}
                  />
                </div>
            ))}
          </div>
        </div>

        {/* Calculation Results */}
        {(totalHeures !== null && montantBrut !== null && totalHeures > 0) && (
            <div className="bg-blue-50 p-4 rounded-lg border border-blue-100">
              <h3 className="text-sm font-medium text-blue-700 mb-2">Résultat du calcul</h3>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <p className="text-sm text-gray-600">Total heures</p>
                  <p className="font-medium text-gray-800">{totalHeures} h</p>
                </div>
                <div>
                  <p className="text-sm text-gray-600">Montant brut</p>
                  <p className="font-medium text-gray-800">{montantBrut.toLocaleString()} FCFA</p>
                </div>
              </div>
            </div>
        )}

        {/* Action Buttons */}
        <div className="flex justify-end gap-3 pt-2">
          <Button
              variant="outline"
              onClick={handleCalcul}
              disabled={!selectedContrat|| !totalHeures}
              className={`text-white border ${
                  selectedContrat
                      ? 'bg-green-500 hover:bg-green-600 border-green-500  hover:border-green-600 hover:text-white'
                      : 'bg-gray-200 border-gray-300'
              }`}
          >
            Calculer
          </Button>
          <Button
              onClick={()=>{setIsOpenAction(true)}}
              disabled={!selectedContrat || !montantBrut || !totalHeures || montantBrut <= 0 || totalHeures <= 0 || !selectedDate}
              className="bg-blue-600 hover:bg-blue-700 text-white"
          >
            Enregistrer
          </Button>
        </div>
        <div>
          <ActionModal
              title="✅ Confimer l'enregistrement de l'heures supplémentaires"
              description={`Être sur de vouloir enregistrer ces heures supplémentaires pour l'employé sélectionné ${selectedContrat?.nom} ${selectedContrat?.prenom} ?\n Total des heurs: ${totalHeures} H \n Montant brut: ${montantBrut} F CFA`}
              isOpen={isOpenAction}
              isetIsOpen={setIsOpenAction}
              isLoading={loadingSubmit}
              onConfirm={() => {
                handleSubmit()
              }}
              onCancel={() => {
                setIsOpenAction(false)
              }}
              confirmText="Confirmer"
              confirmColor="green"

          />
        </div>
      </div>
  );
}