import { useEffect, useState } from "react";
import { useAuth } from "@/lib/auth.ts";
import useCompanyStore from "@/contexts/CompanyContext.ts";
import apiRoutes from "@/api/apiRoutes.ts";
import apiService from "@/api/apiService.ts";
import { Skeleton } from "@/components/ui/skeleton";
import { Button } from "@/components/ui/button";
import { FileTextIcon } from "lucide-react";

import LogItem from "@/components/layout/traitements-salaire/LogItem.tsx";
import useTraitementStore from "@/contexts/useTraitementStore.ts";
import useTabsStore from "@/contexts/useTabsStore.ts";

export type TraitementSalaireType = {
    id: number;
    companyId: number;
    companyName: string;
    departementId: number | null;
    departementName: string | null;
    mois: string;
    statut: "En attente" | "Vérifié" | "Validé" | "Rejeté";
    message: string | null;
    declencheurId: number | null;
    verificateurId: number | null;
    dateVerification: string | null;
    validateurId: number | null;
    dateValidation: string | null;
    rejetParId: number | null;
    dateRejet: string | null;
    motifRejet: string | null;
    dateTraitement: string;
    createdAt: string;
    updatedAt: string;
};

export default function SaveTraitementSalaireTab() {
    const { user, logout } = useAuth();
    const { selectedCompany } = useCompanyStore();
    const { setMoisEtCompany,reset } = useTraitementStore();
    const [loadingPage, setLoadingPage] = useState(false);
    const [listLog, setListLog] = useState<TraitementSalaireType[]>([]);
    const [selectedLog, setSelectedLog] = useState<TraitementSalaireType | null>(null);

    const fetchDatalist = async () => {
        if (!selectedCompany?.id) return;
        setLoadingPage(true);
        try {
            const response = await apiService.post(
                {
                    url: apiRoutes.admin.app.traitementSalaire.all_log,
                    body: JSON.stringify({ companyId: selectedCompany.id }),
                    headers: { "Content-Type": "application/json" },
                },
                {
                    userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "",
                    hasNoSuccessModal: true,
                    onTokenExpired: logout,
                }
            );
            // @ts-ignore
            setListLog(response.data);
        } catch (error) {
            if (error instanceof Error) {
                apiService.handleError(error.message, { hasNoFailureModal: true });
            }
        } finally {
            setLoadingPage(false);
        }
    };

    const onValidate = async () => {
        if (!selectedCompany?.id || !selectedLog) return;
        setLoadingPage(true);
        try {
            await apiService.post(
                {
                    url: apiRoutes.admin.app.traitementSalaire.validation,
                    body: JSON.stringify({
                        companyId: selectedCompany.id,
                        mois: selectedLog.mois,
                        logId: selectedLog.id,
                    }),
                    headers: { "Content-Type": "application/json" },
                },
                {
                    userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "",
                    hasNoSuccessModal: false,
                    onTokenExpired: logout,
                }
            );
            fetchDatalist();
        } catch (error) {
            if (error instanceof Error) {
                apiService.handleError(error.message, { hasNoFailureModal: false });
            }
        } finally {
            setLoadingPage(false);
        }
    };

    const onRestore = async () => {
        if (!selectedCompany?.id || !selectedLog) return;
        setLoadingPage(true);
        try {
            await apiService.post(
                {
                    url: apiRoutes.admin.app.traitementSalaire.reset,
                    body: JSON.stringify({
                        companyId: selectedCompany.id,
                        mois: selectedLog.mois,
                        logId: selectedLog.id,
                    }),
                    headers: { "Content-Type": "application/json" },
                },
                {
                    userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "",
                    hasNoSuccessModal: false,
                    onTokenExpired: logout,
                }
            );
            fetchDatalist();
        } catch (error) {
            if (error instanceof Error) {
                apiService.handleError(error.message, { hasNoFailureModal: false });
            }
        } finally {
            setLoadingPage(false);
        }
    };

    useEffect(() => {
        reset()
        fetchDatalist();

    }, [selectedCompany?.id]);

    return (
        <>
            <div className="flex items-center justify-between mb-4">
                <div>
                    <h2 className="text-lg font-semibold">Validation et sauvegarde</h2>
                    <p className="text-xs font-medium text-muted-foreground">
                        Vérifier, valider et sauvegarder les salaires
                    </p>
                </div>
            </div>

            {loadingPage ? (
                <div className="grid gap-4">
                    {[...Array(4)].map((_, i) => (
                        <div key={i} className="p-4">
                            <Skeleton className="h-6 w-1/2 mb-2" />
                            <Skeleton className="h-4 w-full mb-1" />
                            <Skeleton className="h-4 w-3/4" />
                        </div>
                    ))}
                </div>
            ) : listLog.length === 0 ? (
                <div className="flex flex-col items-center justify-center py-12 space-y-4 text-center border rounded-lg">
                    <FileTextIcon className="h-12 w-12 text-muted-foreground" />
                    <p className="text-lg font-medium">Aucun traitement trouvé</p>
                    <p className="text-sm text-muted-foreground">
                        Aucune opération de paie n'a été enregistrée pour le moment
                    </p>
                    <Button variant="outline" onClick={fetchDatalist}>
                        Réessayer
                    </Button>
                </div>
            ) : (
                <div className="grid gap-4">
                    {listLog.map((log) => (
                        <LogItem
                            key={log.id}
                            log={log}
                            onApercu={(log) => {
                                setMoisEtCompany(log.mois, log.companyId);
                                useTabsStore.getState().setActiveTab("backup-tabs", "preview-after");
                            }}
                            onRejeter={() => {
                                setSelectedLog(log);
                                onRestore();
                            }}
                            onValider={() => {
                                setSelectedLog(log);
                                onValidate();
                            }}
                            onRestaurer={() => {
                                setSelectedLog(log);
                                onRestore();
                            }}
                        />
                    ))}
                </div>
            )}
        </>
    );
}
