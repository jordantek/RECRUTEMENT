import {DynamicTable3} from "@/components/tables/dynamic-table-3.tsx";
import {useEffect, useState} from "react";
import {EmployeeType} from "@/types/employee/EmployeeType.ts";
import apiService from "@/api/apiService.ts";
import apiRoutes from "@/api/apiRoutes.ts";
import {useAuth} from "@/lib/auth.ts";
import { AccidentTravailType} from "@/types/AccidentTravail.ts";
import {DetailDialog} from "@/components/useful/detail-modal.tsx";
import DetailWorkAccident from "@/components/layout/administrative-manager/detail-work-accident.tsx";


interface EmployeeProps {
    employe: EmployeeType;
}

export default function EmployeAccidentsTab({employe}: EmployeeProps) {
    const {user,logout}=useAuth()
    const [accidents,setAccidents]=useState<AccidentTravailType[]>([])
    const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
    const [selectedAccident, setSelectedAccident] = useState<AccidentTravailType | null>(null);
    const [loadingPage, setLoadingPage] = useState(false);

    const OnFetchAccident = async () => {
        setLoadingPage(true);
        try {
            const response = await apiService.get(
                {
                    url: `${apiRoutes.admin.app.accidentsTravail.list_by_employee}${employe.id}`,
                },
                {
                    userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                    hasNoSuccessModal: true,
                    onTokenExpired: logout,
                }
            );

            if (response.data) {
                const accidentData: AccidentTravailType[] = response.data;
                setAccidents(accidentData);
            }
        } catch (error) {
            console.error("Error fetching sanctions:", error);
        } finally {
            setLoadingPage(false);
        }
    };

    const handleViewAccident = (accident: AccidentTravailType) => {
        setSelectedAccident(accident);
        setIsDetailModalOpen(true);
    };

    useEffect(() => {
        OnFetchAccident()
    }, [employe]);

    return (
        <div >
            <DetailDialog isOpen={isDetailModalOpen} setIsOpen={setIsDetailModalOpen}>
                <DetailWorkAccident workAccident={selectedAccident} />
            </DetailDialog>

            {/* Header */}
            <div className="flex items-center justify-between  px-4  mb-4">
                <div className={"text-left"}>
                    <h2 className="text-lg font-semibold">Liste des accidents de travail</h2>
                    <p className="text-xs text-muted-foreground">
                        Consulter la liste des accident de travail de l'employé.
                    </p>
                </div>
            </div>

            {/* Liste des contrats */}
            <div className="px-4">
                {/* À remplacer par votre tableau plus tard */}
               <div className="m-auto w-fullgap-2">
                    <DynamicTable3
                        columns={[
                            { key: "dateAccident", label: "Date d'accident" },
                            { key: "dateDeclaration", label: "Date de Déclaration" },
                            { key: "effetAccident", label: "Effet" },
                            { key: "action", label: "Action" },
                            {
                                key: "depense",
                                label: "Dépense (FCFA)",
                                render: (row) =>
                                    typeof row=== "number"
                                        ? `${row.toLocaleString()} FCFA`
                                        : "—",
                            }
                        ]}
                        data={accidents}
                        isLoading={loadingPage}
                        onRefresh={OnFetchAccident}
                        onView={
                            handleViewAccident
                        }
                    />
                </div>
            </div>
        </div>
    );
}