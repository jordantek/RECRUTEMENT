import { useEffect, useState } from "react";
import { format, isAfter } from "date-fns";
import { fr } from "date-fns/locale";

import { Button } from "@/components/ui/button";
import { toast } from "sonner";
import apiService from "@/api/apiService";
import apiRoutes from "@/api/apiRoutes";
import { useAuth } from "@/lib/auth";
import useCompanyStore from "@/contexts/CompanyContext";
import { MonthYearPicker } from "@/components/inputs/MonthYearPicker";
import {
    Select,
    SelectTrigger,
    SelectValue,
    SelectContent,
    SelectItem,
} from "@/components/ui/select";
import { Skeleton } from "@/components/ui/skeleton";
import ActionModal from "@/components/useful/action-modal";
import { DepartmentType } from "@/types/UtilsTypes";

export default function CalculeSalaireTab() {
    const { user, logout } = useAuth();
    const { selectedCompany } = useCompanyStore();

    const [loadingPage, setLoadingPage] = useState(true);
    const [isFetchSetting, setIsFetchSetting] = useState(false);
    const [departementOptions, setDepartementOptions] = useState<DepartmentType[]>([]);
    const [selectedDate, setSelectedDate] = useState<string | null>(null);
    const [selectedInstitution, setSelectedInstitution] = useState<string | null>(null);
    const [selectedDepartement, setSelectedDepartement] = useState<string | null>(null);
    const [isOpenAction, setIsOpenAction] = useState(false);
    const [loadingSubmit, setLoadingSubmit] = useState(false);

    const onFetchSetting = async () => {
        if (!selectedCompany?.id) return;

        try {
            setIsFetchSetting(true);
            setLoadingPage(true);
            const response = await apiService.get(
                {
                    url: `${apiRoutes.admin.app.company.departement.list_byCompany}`,
                    params: { companyId: selectedCompany.id },
                },
                {
                    userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "",
                    hasNoSuccessModal: true,
                }
            );
            setDepartementOptions(Array.isArray(response.data) ? response.data : []);
        } catch (error) {
            if (error instanceof Error) {
                apiService.handleError(error.message, { hasNoFailureModal: true });
            }
        } finally {
            setIsFetchSetting(false);
            setLoadingPage(false);
        }
    };

    const onSubmitCalcule = async () => {
        if (!selectedCompany?.id || !selectedDate) return;
        setLoadingSubmit(true);

        try {
            await apiService.post(
                {
                    url: apiRoutes.admin.app.traitementSalaire.calcul_salaire,
                    body: JSON.stringify({
                        companyId: selectedCompany.id,
                        mois: selectedDate,
                        departement: selectedDepartement,
                    }),
                    headers: { "Content-Type": "application/json" },
                },
                {
                    userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "",
                    hasNoSuccessModal: false,
                    onTokenExpired: logout,
                }
            );
            setIsOpenAction(false);
        } catch (error) {
            if (error instanceof Error) {
                apiService.handleError(error.message, { hasNoFailureModal: false });
            }
        } finally {
            setLoadingSubmit(false);
        }
    };

    useEffect(() => {
        if (selectedCompany?.id) {
            onFetchSetting();
        }
    }, [selectedCompany?.id]);

    return (
        <>
            <div className="mb-4">
                <h2 className="text-lg font-bold text-gray-800">Calcul des salaires </h2>
                <p className="text-xs text-muted-foreground">
                    Générez et enregistrez les fiches de paie pour une période donnée.
                </p>
            </div>

            <div className="flex flex-col justify-center items-center h-full min-h-[350px] w-full space-y-8">
                {loadingPage ? (
                    <>
                        <div className="text-center space-y-2">
                            <Skeleton className="h-6 w-64 mx-auto" />
                            <Skeleton className="h-4 w-48 mx-auto" />
                        </div>

                        <div className="flex flex-wrap gap-4 justify-center items-center">
                            <Skeleton className="h-10 w-52" />
                            <Skeleton className="h-10 w-60" />
                        </div>

                        <div className="flex gap-4 justify-center">
                            <Skeleton className="h-10 w-32 rounded-md" />
                            <Skeleton className="h-10 w-32 rounded-md" />
                        </div>
                    </>
                ) : (
                    <>
                        <div className="text-center space-y-1">
                            <p className="font-bold text-blue-700">
                                {selectedCompany?.name ?? "Aucune entreprise"}
                            </p>
                        </div>

                        <div className="flex flex-wrap items-center justify-center gap-4">
                            <MonthYearPicker
                                value={selectedDate}
                                onChange={(value) => {
                                    if (!value) return;

                                    const isFuture = isAfter(new Date(`${value}-01`), new Date());

                                    if (isFuture) {
                                        toast.error("Mois invalide sélectionné", {
                                            description: "Le mois choisi est dans le futur. Veuillez en sélectionner un valide.",
                                            position: "top-right",
                                            duration: 5000,
                                            style: {
                                                backgroundColor: "#fef3c7",
                                                border: "1px solid #facc15",
                                                color: "#78350f",
                                                boxShadow: "0 4px 12px rgba(0, 0, 0, 0.08)",
                                                padding: "16px 20px",
                                                borderRadius: "10px",
                                                fontSize: "0.875rem",
                                                fontWeight: 500,
                                            },
                                            icon: "⚠️",
                                        });
                                        setSelectedDate((prev) => prev); // Reset à l’état précédent
                                        setSelectedInstitution(null);
                                        return;
                                    }

                                    setSelectedDate(value);
                                }}
                                placeholder="Choisir un mois"
                            />

                            <Select
                                value={selectedInstitution ?? "all"}
                                onValueChange={(value) => {
                                    setSelectedInstitution(value);
                                    setSelectedDepartement(value === "all" ? null : value);
                                }}
                                disabled={isFetchSetting || !selectedDate}
                            >
                                <SelectTrigger className="w-[240px]">
                                    <SelectValue placeholder="Tous les départements" />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="all">Tous les départements</SelectItem>
                                    {departementOptions.map((dep) => (
                                        <SelectItem key={dep.id} value={dep.id.toString()}>
                                            {dep.libelle}
                                        </SelectItem>
                                    ))}
                                </SelectContent>
                            </Select>
                        </div>

                        <div className="flex justify-center items-center gap-4">
                            <Button
                                className="bg-red-600 hover:bg-red-700 text-white"
                                onClick={() => {
                                    setSelectedDate(null);
                                    setSelectedInstitution(null);
                                }}
                                disabled={!selectedDate}
                            >
                                Réinitialiser
                            </Button>

                            <Button
                                className="bg-green-600 hover:bg-green-700 text-white"
                                onClick={() => setIsOpenAction(true)}
                                disabled={!selectedDate}
                            >
                                Calculer
                            </Button>
                        </div>
                    </>
                )}
            </div>

            <p className="text-xs text-center text-orange-600 italic mt-10">
                ⚠️ Le calcul des salaires ne doit être effectué qu'après traitement des paies du mois concerné.
            </p>

            <ActionModal
                title="✅ Confirmation du calcul et de l'enregistrement"
                description={`Souhaitez-vous confirmer le calcul et l'enregistrement des salaires pour ${
                    selectedDate ? format(new Date(selectedDate + "-01"), "MMMM yyyy", { locale: fr }) : "la période sélectionnée"
                } ?`}
                isOpen={isOpenAction}
                isetIsOpen={setIsOpenAction}
                isLoading={loadingSubmit}
                onConfirm={onSubmitCalcule}
                onCancel={() => setIsOpenAction(false)}
                confirmText="Confirmer"
                confirmColor="green"
            />
        </>
    );
}
