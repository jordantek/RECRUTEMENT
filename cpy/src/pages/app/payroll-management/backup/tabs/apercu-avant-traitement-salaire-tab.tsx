import { useEffect, useState } from "react";
import { format, subMonths, isAfter } from "date-fns";
import { Button } from "@/components/ui/button.tsx";
import {FileText, FileSpreadsheet, X, Printer, FileTextIcon} from "lucide-react";
import { MonthYearPicker } from "@/components/inputs/MonthYearPicker.tsx";
import {
    Select, SelectTrigger, SelectValue, SelectContent, SelectItem,
} from "@/components/ui/select.tsx";
import { DynamicTable3 } from "@/components/tables/dynamic-table-3.tsx";
import { useAuth } from "@/lib/auth.ts";
import useCompanyStore from "@/contexts/CompanyContext.ts";
import apiRoutes from "@/api/apiRoutes.ts";
import apiService from "@/api/apiService.ts";
import {toast} from "@/hooks/use-toast.ts";
import useTabsStore from "@/contexts/useTabsStore.ts";
import {DateHelpers} from "@/helpers/DateHelpers.ts";

interface Department {
    id: number;
    libelle: string;
}

export type ApercuSalaireType = {
    contratEmployeId: number;
    nomPrenomEmploye: string;
    numeroCompte: string;
    mois: string;
    salaireBaseContrat: number;
    heureSup: number;
    tempsTravail: number;
    salaireBrut: number;
    autrePrime: number;
    montantIpts: number;
    montantCnss: number;
    montantTTRetenue: number;
    montantVps: number;
    montantCnssEmployeur: number;
    totalChargePatronale: number;
    salaireNet: number;
    mensualite: number;
    totalRetenue: number;
    netAPayer: number;
    departement: string;
    bank: string;
};

export default function ApercuAvantTraitementSalaireTab() {
    const {user, logout} = useAuth();
    const {selectedCompany} = useCompanyStore();

    const [loadingPage, setLoadingPage] = useState(false);
    const [loadingDepartments, setLoadingDepartments] = useState(false);
    const [salaires, setSalaires] = useState<ApercuSalaireType[]>([]);
    const [departements, setDepartements] = useState<Department[]>([]);
    const [selectedDate, setSelectedDate] = useState<string | null>(format(new Date(), "yyyy-MM"));
    const [selectedDepartement, setSelectedDepartement] = useState<string | null>(null);
    const [errorMessage, setErrorMessage] = useState<string | null>(null);

    const fetchDepartements = async () => {
        if (!selectedCompany?.id) return;
        setLoadingDepartments(true);
        try {
            const res = await apiService.get(
                {
                    url: apiRoutes.admin.app.company.departement.list_byCompany,
                    params: {companyId: selectedCompany.id}
                },
                {
                    userToken: `${user?.type} ${user?.token}`,
                    hasNoSuccessModal: true
                }
            );
            setDepartements([...(res.data || [])]);
        } catch (error) {
            console.error("Erreur récupération départements:", error);
        } finally {
            setLoadingDepartments(false);
        }
    };

    const fetchDatalist = async () => {
        if (!selectedCompany?.id || !selectedDate) return;

        setLoadingPage(true);
        setErrorMessage(null);

        try {
            const res = await apiService.post(
                {
                    url: apiRoutes.admin.app.traitementSalaire.apercu_avant,
                    body: JSON.stringify({
                        companyId: selectedCompany.id,
                        mois: selectedDate,
                        departement: selectedDepartement,
                    }),
                    headers: {"Content-Type": "application/json"},
                },
                {
                    userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "",
                    hasNoSuccessModal: true,
                    onTokenExpired: logout,
                }
            );

            // Supposons que l'API renvoie toujours un tableau ou null
            // @ts-ignore
            setSalaires((res.data as ApercuSalaireType[]) || []);
        } catch (error: any) {
            console.error("Erreur lors de la récupération des salaires :", error);

            let errorObj: any;

            // Si c’est une erreur avec message JSON (comme ton cas)
            if (typeof error.message === "string" && error.message.startsWith("{")) {
                try {
                    errorObj = JSON.parse(error.message);
                } catch (parseErr) {
                    console.error("Erreur lors du parsing du message d'erreur JSON :", parseErr);
                }
            }

            const errorCode = errorObj?.errorCode;

            const errorMessages: Record<string, string> = {
                TRAITEMENT_VALIDE: "Le mois est déjà validé. Impossible de générer un aperçu.",
                MOIS_INVALIDE: "La date choisie n'est pas valide.",
                TRAITEMENT_EXISTANT: "Un traitement existe déjà pour ce mois.",
            };

            const message = errorMessages[errorCode] || "Une erreur est survenue.";
            setErrorMessage(message);

            toast({
                title: "Erreur",
                description: message,
                variant: "destructive",
            });

            setSalaires([]);
        } finally {
            setLoadingPage(false);
        }
    };


    const handleDateChange = (date: string | null) => {
        const formatted = date ? format(date, "yyyy-MM") : null;
        setSelectedDate(formatted);
    };

    const isFutureDate = selectedDate && isAfter(new Date(`${selectedDate}-01`), new Date());

    useEffect(() => {
        fetchDepartements();
    }, []);

    useEffect(() => {
        if (selectedCompany?.id && selectedDate && !isFutureDate) {
            setSalaires([]);
            fetchDatalist();
        } else {
            setErrorMessage(null);
        }
    }, [selectedCompany, selectedDate, selectedDepartement]);

    return (
        <>
        <div className="mb-4">
            <h2 className="text-lg font-semibold">Aperçu avant traitement salaire du mois de {DateHelpers.formatMonthYearShort(selectedDate??"")}</h2>
            <p className="text-xs font-medium text-muted-foreground">
                Données pré-traitement pour validation
            </p>
        </div>

        <div className="flex items-center justify-between mb-4">
            <div className="flex items-center space-x-4">
                <MonthYearPicker
                    value={selectedDate}
                    onChange={handleDateChange}
                    placeholder="Choisir un mois"
                />
                <Select
                    value={selectedDepartement ?? "all"}
                    onValueChange={(value) => setSelectedDepartement(value === "all" ? null : value)}
                    disabled={loadingDepartments}
                >
                    <SelectTrigger className="w-[220px]">
                        <SelectValue placeholder="Tous les départements"/>
                    </SelectTrigger>
                    <SelectContent>
                        <SelectItem value="all">Tous les départements</SelectItem>
                        {departements.map((d) => (
                            <SelectItem key={d.id} value={d.id.toString()}>
                                {d.libelle}
                            </SelectItem>
                        ))}
                    </SelectContent>
                </Select>
                {(selectedDate || selectedDepartement) && (
                    <Button
                        variant="outline"
                        size="sm"
                        onClick={() => {
                            setSelectedDate(format(subMonths(new Date(), 1), "yyyy-MM"));
                            setSelectedDepartement(null);
                            fetchDatalist();
                        }}
                        className="flex items-center gap-1"
                        aria-label="Réinitialiser les filtres"
                    >
                        <X className="h-4 w-4"/>
                    </Button>
                )}
            </div>

            <div className="flex items-center space-x-4">
                <Button
                    variant="outline"
                    size="sm"
                    onClick={() => window.print()}
                    disabled={!!errorMessage || !!isFutureDate}
                    className="text-blue-600 border-blue-600 hover:bg-blue-50 hover:text-blue-700"
                >
                    <Printer className="h-4 w-4 mr-2" />
                    Imprimer
                </Button>

                <Button variant="outline" size="sm"
                        className="text-red-600 border-red-600 hover:bg-red-600 hover:text-white" disabled={!!errorMessage || !!isFutureDate}>
                    <FileText className="h-4 w-4"/>
                    Export PDF
                </Button>
                <Button variant="outline" size="sm"
                        className="text-green-600 border-green-600 hover:bg-green-600 hover:text-white" disabled={!!errorMessage || !!isFutureDate}>
                    <FileSpreadsheet className="h-4 w-4"/>
                    Export Excel
                </Button>
            </div>
        </div>
            {isFutureDate ? (
                <div className="flex flex-col items-center justify-center py-12 space-y-4 text-center rounded-lg">
                    <FileTextIcon className="h-12 w-12 text-destructive" />
                    <p className="text-lg font-medium text-destructive">Erreur</p>
                    <p className="text-sm text-muted-foreground">
                        Impossible de générer un aperçu pour une date future.
                    </p>
                </div>
            ) : errorMessage ? (
                <div className="flex flex-col items-center justify-center py-12 space-y-4 text-center  rounded-lg">
                    <FileTextIcon className="h-12 w-12 text-destructive" />
                    <p className="text-lg font-medium text-destructive">Erreur</p>
                    <p className="text-sm text-muted-foreground">
                        {errorMessage}
                    </p>
                    <Button variant="outline" onClick={()=>{
                        useTabsStore.getState().setActiveTab("backup-tabs", "preview-after")
                    }}>
                        Détail
                    </Button>
                </div>
            ) : (
                <DynamicTable3
                    columns={[
                        {
                            key: "nomPrenomEmploye",
                            label: "Employé",
                            render: (value) => (
                                <div className="flex items-center gap-2">
                                    <p className="w-8 h-8 flex items-center justify-center rounded-full border text-sm font-medium bg-muted text-muted-foreground">
                                        {value?.toString().toUpperCase().charAt(0) || "?"}
                                    </p>
                                    <span className="font-medium">{value}</span>
                                </div>
                            ),
                        },
                        { key: "departement", label: "Département" },
                        {
                            key: "tempsTravail",
                            label: "Jours Travail.",
                            render: (value) => <span>{value} jrs</span>,
                        },
                        {
                            key: "heureSup",
                            label: "Heures supp.",
                            render: (value) => `${value} `,
                        },
                        {
                            key: "autrePrime",
                            label: "Primes",
                            render: (value) => `${value.toLocaleString()} `,
                        },
                        {
                            key: "salaireBaseContrat",
                            label: "Salaire de base",
                            render: (value) => `${value.toLocaleString()} `,
                        },
                        {
                            key: "salaireBrut",
                            label: "Salaire brut",
                            render: (value) => `${value.toLocaleString()} `,
                        },
                        {
                            key: "salaireNet",
                            label: "Salaire net",
                            render: (value) => (
                                <span className="font-semibold text-blue-600">
                        {value.toLocaleString()}
                    </span>
                            ),
                        },
                        {
                            key: "netAPayer",
                            label: "Net à payer",
                            render: (value) => (
                                <span className="font-semibold text-green-700">
                        {value.toLocaleString()}
                    </span>
                            ),
                        },
                    ]}
                    data={salaires}
                    isLoading={loadingPage}
                />
            )}

</>
)
    ;
}

