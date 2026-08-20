import {useEffect, useState} from "react";
import { EmployeeType } from "@/types/employee/EmployeeType.ts";
import { DynamicTable3 } from "@/components/tables/dynamic-table-3.tsx";
import apiService from "@/api/apiService.ts";
import apiRoutes from "@/api/apiRoutes.ts";
import {useAuth} from "@/lib/auth.ts";
import {SanctionType} from "@/types/SanctionType.ts";
import {DetailDialog} from "@/components/useful/detail-modal.tsx";
import DetailSanction from "@/components/layout/administrative-manager/DetailSanction.tsx";

interface EmployeeSanctionsProps {
    employe: EmployeeType;
}

export default function EmployeSanctionsTab({ employe }: EmployeeSanctionsProps) {
    const { user, logout } = useAuth();
    const [loading, setLoading] = useState(false);
    const [sanctions, setSanctions] = useState<SanctionType[]>([]);
    const [selectedSanction, setSelectedSanction] = useState<SanctionType | null>(null);
    const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);

    const OnFetchSanction = async () => {
        setLoading(true);
        try {
            const response = await apiService.get(
                {
                    url: `${apiRoutes.admin.app.sanction.list_by_employee}${employe.id}`,
                },
                {
                    userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                    hasNoSuccessModal: true,
                    onTokenExpired: logout,
                }
            );

            if (response.data) {
                const sanctionsData: SanctionType[] = response.data;
                setSanctions(sanctionsData);
            }
        } catch (error) {
            console.error("Error fetching sanctions:", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        OnFetchSanction()
    }, [employe.id]);

    const handleView = (sanction: SanctionType) => {
        setSelectedSanction(sanction);
        setIsDetailModalOpen(true);
    }

    return (
        <div>
            <DetailDialog title="Détails de la sanction" isOpen={isDetailModalOpen} setIsOpen={setIsDetailModalOpen}>
                <DetailSanction sanction={selectedSanction} />
            </DetailDialog>

            {/* */}
            <div className="flex items-center justify-between px-4 mb-4">
                <div className={"text-left"}>
                    <h2 className="text-lg font-semibold">Historique des sanctions</h2>
                    <p className="text-xs text-muted-foreground">
                        Sanctions de {employe.prenom} {employe.nom}
                    </p>
                </div>
            </div>

            {/**/}
            <div className="px-4">
                <div className="m-auto w-fullgap-2">
                    <DynamicTable3<SanctionType>
                        columns={[
                            { 
                                key: "datePlainte", 
                                label: "Date de P.",
                                render: (date) => new Date(date).toLocaleDateString('fr-FR')
                            },
                            { 
                                key: "contenuePlainte", 
                                label: "Plainte",
                                render: (plainte) => (
                                    <div className="max-w-xs truncate" title={plainte}>
                                        {plainte}
                                    </div>
                                )
                            },
                            { 
                                key: "dateDemandeExplication", 
                                label: "Date de DE.",
                                render: (date) => date ? new Date(date).toLocaleDateString('fr-FR') : 'N/A'
                            },
                            { 
                                key: "dateReponse", 
                                label: "Date de R.",
                                render: (date) => date ? new Date(date).toLocaleDateString('fr-FR') : 'En attente'
                            },
                            { 
                                key: "sanctionDonnee", 
                                label: "Sanction",
                                render: (sanction) => (
                                    <span className={`
                                        inline-flex items-center px-2 py-0.5 rounded text-xs font-medium
                                        ${sanction ? 
                                            sanction.includes("avertissement") ? 'bg-yellow-100 text-yellow-800' :
                                            sanction.includes("pied") ? 'bg-orange-100 text-orange-800' :
                                            'bg-red-100 text-red-800' 
                                          : 'bg-gray-100 text-gray-800'}
                                    `}>
                                        {sanction || 'En cours'}
                                    </span>
                                )
                            },
                        ]}
                        data={sanctions}
                        onRefresh={OnFetchSanction}
                        isLoading={loading}
                        onView={handleView}
                    />
                </div>
            </div>
        </div>
    );
}