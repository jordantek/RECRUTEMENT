import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button.tsx";
import {
  UserPlusIcon,
  Calculator,
  CalendarDays,
  CircleDollarSign,
  SearchIcon,
} from "lucide-react";
import { DynamicTable3 } from "@/components/tables/dynamic-table-3.tsx";
import { OvertimeForm } from "./overtime-form.tsx";
import apiService from "@/api/apiService.ts";
import { useAuth } from "@/lib/auth.ts";
import useCompanyStore from "@/contexts/CompanyContext.ts";
import { format } from "date-fns";
import { fr } from "date-fns/locale";
import { Input } from "@/components/ui/input.tsx";
import { MonthYearPicker } from "@/components/inputs/MonthYearPicker.tsx";
import {DetailDialog} from "@/components/useful/detail-modal.tsx";
import DetailWorkAccident from "@/components/layout/administrative-manager/detail-work-accident.tsx";
import OvertimeDetail from "@/components/layout/paye/overtime-detail.tsx";

export type HeureSupplementaireType = {
  id: number;
  mois: string; // Format: "YYYY-MM"
  salaireBaseContrat: number;
  salaireBrutContrat: number;
  heures12: number;
  heures35: number;
  heures50: number;
  heures100: number;
  majoration12: number;
  majoration35: number;
  majoration50: number;
  majoration100: number;
  totalHeures: number;
  montant: number;
  observation: string;
  contratEmployeId: number;
  employeId: number;
  companyId: number;
  lastUpdateUserId: number;
  createdAt: string;
  updatedAt: string;
  employe?: { nom: string; prenom: string };
};

export function OvertimeTab() {
  const { user, logout } = useAuth();
  const { selectedCompany } = useCompanyStore();
  const [isFormVisible, setIsFormVisible] = useState(false);
  const [overtimeHours, setOvertimeHours] = useState<HeureSupplementaireType[]>([]);
  const [selectedDate, setSelectedDate] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [loading, setLoading] = useState(false);
  const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
  const [selectedOvertimeHour, setSelectedOvertimeHour] = useState<HeureSupplementaireType | null>(null);



  const fetchOvertime = async () => {
    if (!selectedCompany?.id) return;
    try {
      setLoading(true);
      const response = await apiService.get(
          {
            url: `/api/paie/heures-supplementaires/par-entreprise/${selectedCompany.id}`,
          },
          {
            userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
            hasNoSuccessModal: true,
            onTokenExpired: logout,
          }
      );
      setOvertimeHours(response.data || []);
    } catch (error) {
      if (error instanceof Error) {
        apiService.handleError(error.message, { hasNoFailureModal: true });
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOvertime();
  }, [selectedCompany]);

  // Filtrage combiné par nom et mois
  const filteredOvertimeHours = overtimeHours.filter((item) => {
    const fullName = `${item.employe?.nom ?? ""} ${item.employe?.prenom ?? ""}`.toLowerCase();
    const matchName = fullName.includes(searchTerm.toLowerCase());
    const matchDate = selectedDate ? item.mois === selectedDate : true;
    return matchName && matchDate;
  });

  const columns = [
    {
      key: "employe",
      label: "Employé(s)",
      render: (value: { nom: string; prenom: string }) =>
          value ? (
              <div className="flex items-center gap-2">
                <p className="w-8 h-8 flex items-center justify-center rounded-full border text-sm font-medium bg-muted text-muted-foreground">
                  {value.nom?.toString().toUpperCase().charAt(0) || "?"}
                </p>
                <span>{value.nom} {value.prenom}</span>
              </div>
          ) : null,
    },
    {
      key: "mois",
      label: "Mois",
      render: (value: string) => {
        const date = new Date(value + "-01");
        const moisLong = format(date, "LLLL yyyy", { locale: fr });
        return (
            <div className="flex items-center gap-2">
              <CalendarDays className="w-4 h-4 text-blue-500" />
              {moisLong}
            </div>
        );
      },
    },
    {
      key: "heures12",
      label: "Maj. 12%",
      render: (value: number) => <div className="flex items-center gap-2">{value}h</div>,
    },
    {
      key: "heures35",
      label: "Maj. 35%",
      render: (value: number) => <div className="flex items-center gap-2">{value}h</div>,
    },
    {
      key: "heures50",
      label: "Maj. 50%",
      render: (value: number) => <div className="flex items-center gap-2">{value}h</div>,
    },
    {
      key: "heures100",
      label: "Maj. 100%",
      render: (value: number) => <div className="flex items-center gap-2">{value}h</div>,
    },
    {
      key: "totalHeures",
      label: "Total Heures",
      render: (value: number) => (
          <div className="flex items-center gap-2">
            <Calculator className="h-4 w-4 text-purple-500" />
            {value}h
          </div>
      ),
    },
    {
      key: "montant",
      label: "Montant",
      render: (value: number) => (
          <div className="flex items-center gap-2">
            <CircleDollarSign className="h-4 w-4 text-green-600" />
            {value.toLocaleString("fr-FR")} FCFA
          </div>
      ),
    },
  ];

  return (
      <div className="space-y-4 px-4">
        {!isFormVisible ? (
            <>
              <DetailDialog isOpen={isDetailModalOpen} setIsOpen={setIsDetailModalOpen}>
                <OvertimeDetail overtime={selectedOvertimeHour} />
              </DetailDialog>
              {/* Header */}
              <div className="flex items-center justify-between">
                <div className="text-left">
                  <h2 className="text-lg font-semibold text-black">Heures supplémentaires</h2>
                  <p className="text-xs text-muted-foreground">
                    Heures majorées effectuées par les agents.
                  </p>
                </div>

                <Button
                    size="sm"
                    className="flex items-center gap-2 rounded-md bg-primary text-primary-foreground hover:bg-primary/80"
                    onClick={() => setIsFormVisible(true)}
                >
                  <UserPlusIcon className="h-4 w-4 mr-2" />
                  Ajouter une heure supplémentaire
                </Button>
              </div>

              {/* Filtres + Tableau */}
              <div className={"space-y-2"}>
                <div className="flex flex-wrap items-end gap-3">
                  {/* Filtre Nom */}
                  <div className="relative">
                    <Input
                        type="text"
                        placeholder="Rechercher par nom..."
                        className="w-full max-w-xs text-sm peer ps-9"
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                    <div className="text-muted-foreground/80 pointer-events-none absolute inset-y-0 start-0 flex items-center justify-center ps-3 peer-disabled:opacity-50">
                      <SearchIcon size={16} />
                    </div>
                  </div>

                  {/* Filtre Mois */}
                  <div className="min-w-[200px]">
                    <MonthYearPicker
                        value={selectedDate}
                        onChange={setSelectedDate}
                        placeholder="Sélectionner un mois"
                        className="bg-white border-gray-300"
                    />
                  </div>
                </div>

                {/* Tableau */}
                <DynamicTable3
                    columns={columns}
                    data={filteredOvertimeHours}
                    onView={(row) => {
                      setSelectedOvertimeHour(row)
                      setIsDetailModalOpen(true)
                    }}
                    isLoading={loading}
                />
              </div>
            </>
        ) : (
            <OvertimeForm onCancel={() => setIsFormVisible(false)} />
        )}
      </div>
  );
}
