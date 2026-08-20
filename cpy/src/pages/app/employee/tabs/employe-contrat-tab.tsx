import { useEffect, useState } from "react";
import { EmployeeType } from "@/types/employee/EmployeeType.ts";
import { useAuth } from "@/lib/auth.ts";
import apiRoutes from "@/api/apiRoutes.ts";
import apiService from "@/api/apiService.ts";
import { DynamicTable3 } from "@/components/tables/dynamic-table-3.tsx";
import { ContratEmploye } from "@/types/ContratType.ts";
import { Loader2 } from "lucide-react";
import DetailContrat from "@/components/layout/company/detail-contrat.tsx";
import {DetailDialog} from "@/components/useful/detail-modal.tsx";

interface EmployeeContratProps {
    emmploye: EmployeeType;
}

export default function EmployeContratTab({ emmploye }: EmployeeContratProps) {
    const { user } = useAuth();
    const [contrats, setContrats] = useState<ContratEmploye[]>([]);
    const [loadingContrat, setLoadingContract] = useState(false);
    const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
    const [selectedContrat, setSelectedContrat] = useState<ContratEmploye | null>(null);

    const fetchContract = async () => {
        try {
            setLoadingContract(true);
            const response = await apiService.get(
                {
                    url: `${apiRoutes.admin.app.employee.contrats.list}${emmploye.id}`,
                },
                {
                    userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                    hasNoSuccessModal: true,
                }
            );
            setContrats(response.data);
        } catch (error) {
            if (error instanceof Error) {
                apiService.handleError(error.message, { hasNoFailureModal: true });
            }
        } finally {
            setLoadingContract(false);
        }
    };

    const handleView = (contrat: ContratEmploye) => {
        setSelectedContrat(contrat);
        setIsDetailModalOpen(true);
    };

    const handleOnDownload = (contrat: ContratEmploye) => {
        console.log("onDownload contrat", contrat.id);
    };

    useEffect(() => {
        setContrats([]);
        fetchContract();
    }, [emmploye]);

    return (
        <div>
            <DetailDialog
                isOpen={isDetailModalOpen}
                setIsOpen={setIsDetailModalOpen}
                size={"xl"}
                title={"Détails du contrat"}
                description={"Informations sur le contrat"}
            >
                <DetailContrat contrat={selectedContrat} />
            </DetailDialog>

            {/* Header - uniformisé avec les autres onglets */}
            <div className="flex items-center justify-between px-4 mb-4">
                <div className="text-left">
                    <h2 className="text-lg font-semibold">Liste des contrats de l'employé</h2>
                    <p className="text-xs text-muted-foreground">
                        Contrats de {emmploye.prenom} {emmploye.nom}
                    </p>
                </div>
            </div>

            {/* Contenu - structure uniformisée */}
            <div className="px-4">
                <div className="m-auto w-fullga p-2">
                    {loadingContrat ? (
                        <div className="flex flex-col items-center justify-center h-32 gap-2">
                            <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
                            <span className="text-sm text-muted-foreground">
                                Chargement des contrats...
                            </span>
                        </div>
                    ) : (
                        <DynamicTable3<ContratEmploye>
                            columns={[
                                { key: "company.name", label: "Entreprise" },
                                { key: "departement.libelle", label: "Département" },
                                { key: "poste.libelle", label: "Poste" },
                                { 
                                    key: "dateDebut", 
                                    label: "Date début",
                                    render: (date) => new Date(date).toLocaleDateString('fr-FR')
                                },
                                { 
                                    key: "dateFin", 
                                    label: "Date fin",
                                    render: (date) => date ? new Date(date).toLocaleDateString('fr-FR') : 'Indéterminée'
                                },
                                {
                                    key: "statusContrattatus",
                                    label: "Statut",
                                    render: () => (
                                        <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-emerald-100 text-emerald-800">
                                            En cours
                                        </span>
                                    ),
                                },
                            ]}
                            data={contrats}
                            onDownload={handleOnDownload}
                            onView={handleView}
                            onRefresh={fetchContract}
                        />
                    )}
                </div>
            </div>
        </div>
    );
}