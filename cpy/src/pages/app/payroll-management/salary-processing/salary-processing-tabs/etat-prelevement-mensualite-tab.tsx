import { Button } from "@/components/ui/button.tsx";
import {FileSpreadsheet, FileText, X} from "lucide-react";
import apiService from "@/api/apiService.ts";
import apiRoutes from "@/api/apiRoutes.ts";
import { useEffect, useState } from "react";
import { useAuth } from "@/lib/auth.ts";
import useCompanyStore from "@/contexts/CompanyContext.ts";
import { InstitutionType } from "@/types/MensualiteType";
import { MonthYearPicker } from "@/components/inputs/MonthYearPicker.tsx";
import {
    Select,
    SelectTrigger,
    SelectValue,
    SelectContent,
    SelectItem,
} from "@/components/ui/select";
import { DynamicTable3 } from "@/components/tables/dynamic-table-3.tsx";
// FIX: Import date-fns for handling date conversions
import { format} from "date-fns";

export interface PrelevementMensuelType {
    id: number;
    datePrelevement: string;
    moisPrelevement: string;
    montant: number;
    employeId: number;
    employeNomComplet: string;
    institutionNom: string;
    contratEmployeId: number;
    companyId: number;
    institutionId: number;
    mensualiteId: number;
    lastUpdateUserId: number;
    createdAt: string;
    updatedAt: string;
}


export default function EtatPrelevementMensualiteTab() {
    const { user, logout } = useAuth();
    const { selectedCompany } = useCompanyStore();
    const [loadingPage, setLoadingPage] = useState(false);
    const [prelevementMensuels, setPrelevementMensuels] = useState<PrelevementMensuelType[]>([]);
    const [isFetchSetting, setIsFetchSetting] = useState(false);
    const [institutionOptions, setInstitutionOptions] = useState<InstitutionType[]>([]);
    const [selectedDate, setSelectedDate] = useState<string | null>(null); // Stays as string for the API
    const [selectedInstitution, setSelectedInstitution] = useState<string | null>(null);

    const onFetchSetting = async () => {
        try {
            setIsFetchSetting(true);
            const response = await apiService.get(
                { url: apiRoutes.admin.app.institutionFinancier.list },
                {
                    userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "",
                    hasNoSuccessModal: true,
                }
            );
            setInstitutionOptions(response.data || []);
        } catch (error) {
            if (error instanceof Error) {
                apiService.handleError(error.message, { hasNoFailureModal: true });
            }
        } finally {
            setIsFetchSetting(false);
        }
    };

    const OnFetchDataAll = async () => {
        if (!selectedCompany?.id) return;

        setLoadingPage(true);
        try {
            const response = await apiService.get(
                {
                    url: `${apiRoutes.admin.app.prelevementMensuel.list_byCompany}${selectedCompany.id}`,
                },
                {
                    userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                    hasNoSuccessModal: true,
                    onTokenExpired: logout,
                }
            );
            // @ts-ignore
            setPrelevementMensuels(response.data?.prelevements ?? []);
        } catch (error) {
            console.error("Error fetching prélèvements mensuels:", error);
            if (error instanceof Error) {
                apiService.handleError(error.message, { hasNoFailureModal: true });
            }
        } finally {
            setLoadingPage(false);
        }
    };

    const OnFetchData = async () => {
        if (!selectedCompany?.id) return;

        setLoadingPage(true);
        try {
            const response = await apiService.post(
                {
                    url: apiRoutes.admin.app.prelevementMensuel.list,
                    body: JSON.stringify({
                        mois: selectedDate ?? "",
                        companyId: selectedCompany.id,
                        institutionId: selectedInstitution,
                    }),
                    headers: { "Content-Type": "application/json" },
                },
                {
                    userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                    hasNoSuccessModal: true,
                    onTokenExpired: logout,
                }
            );
             // @ts-ignore
            setPrelevementMensuels(response.data?.prelevements ?? []);
        } catch (error) {
            console.error("Error fetching prélèvements mensuels:", error);
            if (error instanceof Error) {
                apiService.handleError(error.message, { hasNoFailureModal: true });
            }
            setPrelevementMensuels([]);
        } finally {
            setLoadingPage(false);
        }
    };

    useEffect(() => {
        onFetchSetting();
    }, []);

    // Calcule le total des montants
    const montantTotal = (prelevementMensuels ?? []).reduce(
        (acc, item) => acc + (typeof item.montant === "number" ? item.montant : 0),
        0
    );

    useEffect(() => {
        if (selectedCompany?.id) {
            OnFetchDataAll();
        }
    }, [selectedCompany?.id]);

    useEffect(() => {
        if (selectedCompany?.id) {
            OnFetchData();
        }
    }, [selectedInstitution, selectedDate]);

    const handleDateChange = (date: string | null) => {
        setSelectedDate(date ? format(date, "yyyy-MM") : null);
    };

    return (
        <>
            <div className="flex items-center justify-between mb-4">
                <div>
                    <h2 className="text-lg font-semibold">État des prélèvements mensuels</h2>
                    <p className="text-xs font-medium text-muted-foreground">
                        Suivi détaillé des mensualités par employé
                    </p>
                </div>
            </div>

            <div className="flex items-center justify-between mb-2">
                <div className="flex items-center justify-start space-x-4">
                    <MonthYearPicker

                        value={selectedDate ?? null}

                        onChange={handleDateChange}
                        placeholder="Choisir un mois"
                    />

                    <Select
                        value={selectedInstitution ?? "all"}
                        onValueChange={(value) => {
                            setSelectedInstitution(value === "all" ? null : value);
                        }}
                        disabled={isFetchSetting || !selectedDate}
                    >
                        <SelectTrigger className={"w-[220px]"}>
                            <SelectValue placeholder="Toutes les institutions"/>
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value="all">Toutes les institutions</SelectItem>
                            {institutionOptions.map((institution) => (
                                <SelectItem key={institution.id} value={institution.id.toString()}>
                                    {institution.name}
                                </SelectItem>
                            ))}
                        </SelectContent>
                    </Select>

                    {selectedDate && (
                        <Button
                            variant="outline"
                            size="sm"
                            onClick={() => {
                                setSelectedDate(null)
                                setSelectedInstitution(null)
                                OnFetchDataAll()
                            }}
                            className="flex items-center gap-1"
                            aria-label="Réinitialiser les filtres"
                        >
                            <X className="h-4 w-4"/>
                            {/*Réinitialiser*/}
                        </Button>
                    )}
                </div>

                <div className={"flex items-center justify-end space-x-4"}>
                    <Button
                        variant="outline"
                        size="sm"
                        className="text-red-600 border-red-600 hover:bg-red-600 hover:text-white"
                        onClick={() => {
                            /* TODO: handle export PDF */
                        }}
                    >
                        <FileText className="h-4 w-4"/>
                        Export PDF
                    </Button>

                    <Button
                        variant="outline"
                        size="sm"
                        className="text-green-600 border-green-600 hover:bg-green-600 hover:text-white"
                        onClick={() => {
                            /* TODO: handle export Excel */
                        }}
                    >
                        <FileSpreadsheet className="h-4 w-4"/>
                        Export Excel
                    </Button>
                </div>
            </div>
            <div className="flex  items-center justify-end space-x-2 font-semibold text-sm text-green-300 invisible">
                <span>Montant Total:</span>
                <span>
                    {montantTotal.toLocaleString(undefined, {
                        style: "currency",
                        currency: "XOF",
                        minimumFractionDigits: 0,
                        maximumFractionDigits: 0,
                    })}
                  </span>
            </div>
            <div>
                <DynamicTable3
                    columns={
                        [
                            {
                                key: "employeNomComplet",
                                label: "Employé",
                                render: (value) =>
                                    value ? (
                                        <div className="flex items-center gap-2">
                                            <p className="w-8 h-8 flex items-center justify-center rounded-full border text-sm font-medium bg-muted text-muted-foreground">
                                                {value?.toString().toUpperCase().charAt(0) || '?'}
                                            </p>
                                            <span className="font-medium">{value}</span>
                                        </div>
                                    ) : null,
                            },

                            {
                                key: "moisPrelevement",
                                label: "Mois",
                            },
                            {
                                key: "institutionNom",
                                label: "Institution",
                            },
                            {
                                key: "montant",
                                label: "Montant",
                                render: (value) => {
                                    return typeof value === "number"
                                        ? `${value.toLocaleString()} FCFA`
                                        : "—";
                                }
                            },
                        ]
                    }
                    data={prelevementMensuels}
                    isLoading={loadingPage}

                />
            </div>
        </>
    );
}
