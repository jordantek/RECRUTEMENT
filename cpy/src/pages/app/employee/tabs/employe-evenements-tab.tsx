import {DynamicTable3} from "@/components/tables/dynamic-table-3.tsx";
import {useEffect, useState} from "react";
import {EmployeeType} from "@/types/employee/EmployeeType.ts";
import useCompanyStore from "@/contexts/CompanyContext.ts";
import apiService from "@/api/apiService.ts";
import {useAuth} from "@/lib/auth.ts";
import {EvenementSocialType} from "@/types/EvenementSocialType.ts";
import {DetailDialog} from "@/components/useful/detail-modal.tsx";
import DetailEvenSocial from "@/components/layout/administrative-manager/detail-even-social.tsx";
import apiRoutes from "@/api/apiRoutes.ts";

interface EmployeeProps {
    employe: EmployeeType;
}

export default function EmployeEvenementsTab({employe}: EmployeeProps) {
    const {user, logout} = useAuth()
    const { selectedCompany } = useCompanyStore();

    const [evenements, setEvenements] = useState<EvenementSocialType[]>([])
    const [selectedEvent, setSelectedEvent] = useState<EvenementSocialType | null>(null);
    const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
    const [loadingPage, setLoadingPage] = useState(false);


    const handleView = (evenement: EvenementSocialType) => {
        setSelectedEvent(evenement);
        setIsDetailModalOpen(true);
    }

    const OnFetchEvents = async () => {
        if (!selectedCompany?.id) return;

        setLoadingPage(true);
        try {
            const response = await apiService.get(
                {
                    url: `${apiRoutes.admin.app.evenementsSociaux.list_by_employee}${employe.id}`,
                },
                {
                    userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                    hasNoSuccessModal: true,
                    onTokenExpired: logout,
                }
            );

            if (response.data) {
                const eventData: EvenementSocialType[] = response.data;
                setEvenements(eventData);
            }
        } catch (error) {
            if (error instanceof Error) {
                apiService.handleError(error.message, {hasNoFailureModal: false});
            } else {
                console.log("Une erreur inconnue est survenue");
            }
        } finally {
            setLoadingPage(false);
        }
    };

    useEffect(() => {
        OnFetchEvents();
    }, [employe]);

    return (
        <div>
            <DetailDialog isOpen={isDetailModalOpen} setIsOpen={setIsDetailModalOpen}  title={"Détail de l'événement"} description={"Informations de l'événement"}>
                <DetailEvenSocial evenSocial={selectedEvent} />
            </DetailDialog>

            {/* Header */}
            <div className="flex items-center justify-between px-4 mb-4">
                <div className={"text-left"}>
                    <h2 className="text-lg font-semibold">Liste des événements sociaux</h2>
                    <p className="text-xs text-muted-foreground">
                        Gérez les événements sociaux de votre entreprise
                    </p>
                </div>

            </div>

            {/* Liste des événements */}
            <div className="px-4 ">
                <div className="m-auto w-full gap-2">
                    <DynamicTable3
                        columns={[
                            { key: "dateEvenement", label: "Date de l'événement" },
                            { key: "designation", label: "Désignation" },
                            { key: "actionMenee", label: "Action menée" },
                            {
                                key: "montant",
                                label: "Montant (FCFA)",
                                render: (row) =>
                                    typeof row === "number"
                                        ? `${row.toLocaleString()} FCFA`
                                        : "—",
                            },

                        ]}
                        isLoading={loadingPage}
                        data={evenements}
                        onView={
                            handleView
                        }
                    />
                </div>
            </div>
        </div>
    );
}