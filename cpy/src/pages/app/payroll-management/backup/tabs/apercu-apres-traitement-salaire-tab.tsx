import { Button } from "@/components/ui/button.tsx";
import {FileText, FileSpreadsheet, X, Printer, FileTextIcon} from "lucide-react";
import { useEffect, useState } from "react";
import { MonthYearPicker } from "@/components/inputs/MonthYearPicker.tsx";
import {Select,SelectTrigger,SelectValue,SelectContent,SelectItem} from "@/components/ui/select.tsx";
import {format, isAfter, subMonths} from "date-fns";
import { useAuth } from "@/lib/auth.ts";
import useCompanyStore from "@/contexts/CompanyContext.ts";
import apiRoutes from "@/api/apiRoutes.ts";
import apiService from "@/api/apiService.ts";
import {EmployeeType} from "@/types/employee/EmployeeType.ts";
import {BankType} from "@/types/UtilsTypes.ts";
import useTraitementStore from "@/contexts/useTraitementStore.ts";
import {DynamicTable4} from "@/components/tables/DynamicTable4.tsx";
import useTabsStore from "@/contexts/useTabsStore.ts";
import {StatusBadge} from "@/components/useful/StatusBadge.tsx";
import { toast } from 'sonner';
import {DateHelpers} from "@/helpers/DateHelpers.ts";
import {UtilsHelpers} from "@/helpers/UtilsHelpers.ts";



interface Department {
    id: number;
    libelle: string;
}

export type JsonDescription = {
    contratEmployeId: number;
    nomPrenomEmploye: string;
    numeroCompte: string | null;
    mois: string;
    salaireBaseContrat: number;
    heuresSup: number;
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
    departement: string | null;
    bank: string | null;
};


export interface BulletinPaieDTO {
    id: number;
    dateCalculSalaire: string;
    mois: string;
    contratEmployeId: number | null;
    employeId: number;
    employe?: EmployeeType;
    companyId: number;
    company?: any; // Remplacer par un type `Company` si disponible
    banqueId: number;
    banque?: BankType;
    addedById: number | null;
    tempsTravail: number;
    salaireBrut: number;
    salaireBrutArrondi: number;
    montantCnss: number;
    nombreEnfant: number;
    montantIpts: number;
    montantAib: number;
    totalRetenue: number;
    salaireNet: number;
    autreAvantage: number;
    autreRetenue: number;
    montantCnssEmployeur: number;
    montantVps: number;
    totalChargePatronale: number;
    netAPayer: number;
    numeroCompteEmploye: string;
    congePris: number;
    soldeConge: number;
    signataire: string;
    statut: string;

    validatedAt: string | null;
    validatedBy: number | null;
    validatedByUser?: any; // Remplacer par un type `User` si disponible

    description:string|null,
    jsonDescription: JsonDescription | null;

    createdAt: string;
    updatedAt: string;
    deletedAt: string | null;
}

export default function ApercuApresTraitementSalaireTab() {
    const { user, logout } = useAuth();
    const { selectedCompany } = useCompanyStore();
    const {mois}=useTraitementStore();
    const [loadingPage, setLoadingPage] = useState(false);
    const [loadingDepartments, setLoadingDepartments] = useState(false);
    const [salaires, setSalaires] = useState<BulletinPaieDTO[]>([]);
    const [departements, setDepartements] = useState<Department[]>([]);
    const [selectedDate, setSelectedDate] = useState<string | null>(mois||format(new Date(), "yyyy-MM"));
    const [selectedDepartement, setSelectedDepartement] = useState<string | null>(null);
    const [errorCode, setErrorCode] = useState<string | null>(null);
    const [errorMessage, setErrorMessage] = useState<string | null>(null);

    const fetchDepartements = async () => {
        if (!selectedCompany?.id) return;
        setLoadingDepartments(true);
        try {
            const deptResponse = await apiService.get(
                {
                    url: apiRoutes.admin.app.company.departement.list_byCompany,
                    params: { companyId: selectedCompany.id }
                },
                {
                    userToken: `${user?.type} ${user?.token}`,
                    hasNoSuccessModal: true
                }
            );
            setDepartements([...(deptResponse.data || [])]);
        } catch (error) {
            console.error("Erreur récupération départements:", error);
        } finally {
            setLoadingDepartments(false);
        }
    };

    const fetchDatalist = async () => {
        if (!selectedCompany?.id || !selectedDate) return;

        setLoadingPage(true);
        setErrorCode(null);
        setErrorMessage(null);

        try {
            const response = await apiService.post(
                {
                    url: apiRoutes.admin.app.traitementSalaire.apercu_apres,
                    body: JSON.stringify({
                        companyId: selectedCompany.id,
                        mois: selectedDate,
                        departement: selectedDepartement,
                    }),
                    headers: { "Content-Type": "application/json" },
                },
                {
                    userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "",
                    hasNoSuccessModal: true,
                    onTokenExpired: logout,
                }
            );


            // Parsing de jsonDescription pour chaque bulletin
            // @ts-ignore
            const rawData = response.data as BulletinPaieDTO[] || [];

            const dataParsed = rawData.map((item) => ({
                ...item,
                jsonDescription:
                    typeof item.description === "string"
                        ? JSON.parse(item.description)
                        : item.jsonDescription,
            }));
            // @ts-ignore
            setSalaires(dataParsed as BulletinPaieDTO[] || []);


        } catch (error: any) {
            setSalaires([]);

            try {
                const parsed = JSON.parse(error.message);
                if (parsed?.errorCode) {
                    setErrorCode(parsed.errorCode);
                    setErrorMessage(parsed.message || "Erreur lors du traitement.");
                }
            } catch {
                setErrorMessage("Erreur inattendue. Veuillez réessayer.");
            }

            apiService.handleError(error.message, { hasNoFailureModal: true });
        } finally {
            setLoadingPage(false);
        }
    };
    const isFutureDate = selectedDate && isAfter(new Date(`${selectedDate}-01`), new Date());
    useEffect(() => {
        fetchDepartements();
    }, []);

    useEffect(() => {
        if (selectedCompany?.id && selectedDate && !isFutureDate) {
            setSalaires([]);
            fetchDatalist().then(r => console.log(r));
        }else {
            setErrorMessage(null);
        }
    }, [selectedCompany, selectedDate, selectedDepartement]);

    /*const handleDateChange = (date: string | null) => {
        setSelectedDate(date ? format(date, "yyyy-MM") : null);
    };*/

    return (
        <>
            {/* Header */}
            <div className="flex items-center justify-between mb-4">
                <div>
                    <h2 className="text-lg font-semibold">Aperçu après traitement salaire du mois de {DateHelpers.formatMonthYearShort(selectedDate??"")}</h2>
                    <p className="text-xs font-medium text-muted-foreground">
                        Données post-traitement pour validation
                    </p>
                </div>
                <div>
                    {salaires?.length > 0 && <StatusBadge status={salaires[0].statut}/>}
                </div>
            </div>

            {/* Filtres */}
            <div className="flex items-center justify-between mb-2">
                <div className="flex items-center justify-start space-x-4">
                    <MonthYearPicker
                        value={selectedDate}
                        onChange={(value) => {
                            if (!value) return;

                            const isFuture = isAfter(new Date(`${value}-01`), new Date());

                            if (isFuture) {
                                // Ne change rien, juste afficher l'erreur
                                toast.error("Mois invalide sélectionné", {
                                    description: "Le mois choisi est dans le futur. Veuillez en sélectionner un valide.",
                                    position: "top-right",
                                    duration: 5000,
                                    style: {
                                        backgroundColor: "#fef3c7",           // bg-amber-100
                                        border: "1px solid #facc15",          // amber-400
                                        color: "#78350f",                     // amber-900
                                        boxShadow: "0 4px 12px rgba(0, 0, 0, 0.08)",
                                        padding: "16px 20px",
                                        borderRadius: "10px",
                                        fontSize: "0.875rem",
                                        fontWeight: 500,
                                    },
                                    icon: "⚠️",
                                });
                                setSelectedDate((prev) => prev);
                                return;
                            }

                            // Date OK : on la change
                            setSelectedDate(value);
                        }}
                        placeholder="Choisir un mois"
                    />

                    <Select
                        value={selectedDepartement ?? "all"}
                        onValueChange={(value) =>
                            setSelectedDepartement(value === "all" ? null : value)
                        }
                        disabled={loadingDepartments}
                    >
                        <SelectTrigger className="w-[220px]">
                            <SelectValue
                                placeholder={
                                    loadingDepartments
                                        ? "Chargement..."
                                        : "Tous les départements"
                                }
                            />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value="all">Tous les départements</SelectItem>
                            {departements.map((dept) => (
                                <SelectItem key={dept.id} value={dept.libelle}>
                                    {dept.libelle}
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
                            <X className="h-4 w-4" />
                        </Button>
                    )}
                </div>

                {/* Actions */}
                <div className="flex items-center justify-end space-x-4">
                    <Button
                        variant="outline"
                        size="sm"
                        className="text-blue-600 border-blue-600 hover:bg-blue-600 hover:text-white"
                        onClick={() => window.print()}
                        disabled={!!errorMessage}
                    >
                        <Printer className="h-4 w-4 mr-2" />
                        Imprimer
                    </Button>

                    <Button
                        variant="outline"
                        size="sm"
                        className="text-red-600 border-red-600 hover:bg-red-600 hover:text-white"
                        disabled={!!errorMessage}
                    >
                        <FileText className="h-4 w-4" />
                        Export PDF
                    </Button>

                    <Button
                        variant="outline"
                        size="sm"
                        className="text-green-600 border-green-600 hover:bg-green-600 hover:text-white"
                        disabled={!!errorMessage}
                    >
                        <FileSpreadsheet className="h-4 w-4" />
                        Export Excel
                    </Button>
                </div>
            </div>

            {/* Contenu */}
            {isFutureDate ? (
                <div className="flex flex-col items-center justify-center py-12 space-y-4 text-center rounded-lg">
                    <FileTextIcon className="h-12 w-12 text-destructive" />
                    <p className="text-lg font-medium text-destructive">Erreur</p>
                    <p className="text-sm text-muted-foreground">
                        Impossible de générer un aperçu pour une date future.
                    </p>
                </div>
            ) :errorMessage ? (
                <div className="flex flex-col items-center justify-center py-12 space-y-4 text-center rounded-lg">
                    <FileText className="h-12 w-12 text-destructive" />
                    <p className="text-lg font-medium text-destructive">Erreur</p>
                    <p className="text-sm text-muted-foreground">{errorMessage}</p>
                    <Button variant="outline"  onClick={()=>{
                        useTabsStore.getState().setActiveTab("backup-tabs", "calculation")
                    }}>
                        Calculer les salaires
                    </Button>
                </div>
            ) : (
                <DynamicTable4
                    columns={[
                        {
                            key: "employe",
                            label: "Employé",
                            render: (value) => (
                                <div className="flex items-center gap-2">
                                    <p className="w-8 h-8 flex items-center justify-center rounded-full border text-sm font-medium bg-muted text-muted-foreground">
                                        {value?.nom?.toUpperCase().charAt(0) || "?"}
                                    </p>
                                    <span className="font-medium">
                                    {value?.nom} {value?.prenom}
                                  </span>
                                </div>
                            ),
                            fixed: true,
                        },
                        {
                            key: "banque",
                            label: "Banque",
                            render: (value) => <div>{value.name}</div>,
                        },
                        {
                            key: "numeroCompteEmploye",
                            label: "N° Compte",
                        },
                        {
                            key: "mois",
                            label: "Mois",
                            render: (value: string) => {
                                return DateHelpers.formatMonthYearShort(value)
                            },
                        },
                        {
                            key: "tempsTravail",
                            label: "Temps Travail.",
                        },

                        {
                            key: "jsonDescription",
                            label: "Heures Sup.",
                            render: (value) => value.heuresSup,
                        },
                        {
                            key: "salaireBrut",
                            label: "S. Base",
                        },

                        {
                            key: "jsonDescription",
                            label: "Primes",
                            render: (value) => value.autreAvantage,
                        },
                        {
                            key: "jsonDescription",
                            label: "S. Brut(M-1)",
                            render: (value) => value.salaireButPrecedant,
                        },
                        {
                            key: "salaireBrutArrondi",
                            label: "S. Brut",
                        },
                        {
                            key: "montantIpts",
                            label: "ITS",
                        },
                        {
                            key: "montantCnss",
                            label: "CNSS",
                        },
                        {
                            key: "totalChargeEmploye",
                            label: "Total Charge E.",
                            render: (_, row) =>
                                ((row?.montantCnss ?? 0) + (row?.montantIpts ?? 0)).toLocaleString(),
                        },
                        {
                            key: "salaireNet",
                            label: "Net",
                            render: (value) => (
                                <span className="font-medium text-green-600">
                              {value?.toLocaleString()}
                            </span>
                            ),
                        },
                        {
                            key: "jsonDescription",
                            label: "Mensualités",
                            render: (value) => (
                                <span className="font-medium text-red-500">
                                  {UtilsHelpers.formatMontantWithSeparator(value.mensualite,{showCurrency:false})}
                                </span>
                            ),
                        },
                        {
                            key: "jsonDescription",
                            label: "Avance",
                            render: (value) => (
                                <span className="font-medium text-red-500">
                                  {0}
                                </span>
                            ),
                        },
                        {
                            key: "jsonDescription",
                            label: "Accompte",
                            render: (value) => (
                                <span className="font-medium text-red-500">
                                  {0}
                                </span>
                            ),
                        },
                        {
                            key: "netAPayer",
                            label: "Net à payer",
                            render: (value) => (
                                <span className="font-medium text-green-600">
                                  {value?.toLocaleString()}
                                </span>
                            ),
                        },
                        {
                            key: "montantVps",
                            label: "VPS",
                        },
                        {
                            key: "montantCnssEmployeur",
                            label: "CNSS P.",
                        },
                        {
                            key: "totalChargePatronale",
                            label: "Total P.P.",
                        },
                    ]}
                    data={salaires}
                    isLoading={loadingPage}
                    calculateTotals={(data, columns) => {
                        const totals: Record<string, any> = {};

                        columns.forEach((col) => {
                            if (col.key === "totalChargeEmploye") {
                                totals[col.key] = data.reduce(
                                    (acc, row) => acc + ((row.montantCnss ?? 0) + (row.montantIpts ?? 0)),
                                    0
                                );
                            } else if (col.key === "jsonDescription" && col.label === "Heures Sup.") {
                                // Cas spécifique pour jsonDescription.heuresSup
                                totals["jsonDescription.heuresSup"] = data.reduce(
                                    (acc, row) => acc + (row.jsonDescription?.heuresSup ?? 0),
                                    0
                                );
                            } else {
                                const total = data.reduce((acc, row) => {
                                    // @ts-ignore
                                    const val = row[col.key];
                                    return acc + (typeof val === "number" ? val : 0);
                                }, 0);

                                if (total > 0) totals[col.key] = total;
                            }
                        });

                        Object.keys(totals).forEach((k) => {
                            totals[k] = totals[k].toLocaleString();
                        });

                        return totals;
                    }}
                />
            )}
        </>
    );
}

