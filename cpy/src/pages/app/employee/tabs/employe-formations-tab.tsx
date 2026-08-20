import { useEffect, useState } from "react";
import { EmployeeType } from "@/types/employee/EmployeeType";
import { DynamicTable3 } from "@/components/tables/dynamic-table-3";
import { FormationType } from "@/types/UtilsTypes";
import apiService from "@/api/apiService";
import apiRoutes from "@/api/apiRoutes";
import { useAuth } from "@/lib/auth";

interface EmployeeFormationsProps {
    employe: EmployeeType;
}

export default function EmployeFormationsTab({ employe }: EmployeeFormationsProps) {
    const { user, logout } = useAuth();
    const [formations, setFormations] = useState<FormationType[]>([]);
    const [isLoading, setIsLoading] = useState(false);

    const fetchFormations = async () => {
        setIsLoading(true);
        try {
            const response = await apiService.get(
                {
                    url: `${apiRoutes.admin.app.employee.formations.list_by_employee}${employe.id}`,
                },
                {
                    userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                    hasNoSuccessModal: true,
                    onTokenExpired: logout,
                }
            );

            if (response.data) {
                setFormations(response.data);
            }
        } catch (error) {
            if (error instanceof Error) {
                apiService.handleError(error.message, { hasNoFailureModal: false });
            } else {
                console.error("Une erreur inconnue est survenue");
            }
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        fetchFormations();
    }, [employe]);

    return (
        <div>
            {/*  */}
            <div className="flex items-center justify-between px-4 mb-4">
                <div className={"text-left"}>
                    <h2 className="text-lg font-semibold">Historique des formations</h2>
                    <p className="text-xs text-muted-foreground">
                        Formations de {employe.prenom} {employe.nom}
                    </p>
                </div>
            </div>

            {/*  */}
            <div className="px-4">
                <div className="m-auto w-fullgap-2">
                    <DynamicTable3<FormationType>
                        columns={[
                            {
                                key: "theme",
                                label: "Thème",
                                render: (theme) => (
                                    <div className="font-medium text-foreground">{theme}</div>
                                ),
                            },
                            {
                                key: "lieu",
                                label: "Lieu",
                                render: (lieu) => (
                                    <div className="text-sm text-muted-foreground">{lieu}</div>
                                ),
                            },
                            {
                                key: "dateDebut",
                                label: "Date de début",
                                render: (date) =>
                                    new Date(date).toLocaleDateString("fr-FR"),
                            },
                            {
                                key: "dateFin",
                                label: "Date de fin",
                                render: (date) =>
                                    new Date(date).toLocaleDateString("fr-FR"),
                            },
                            {
                                key: "duree",
                                label: "Durée (jours)",
                                render: (duree) =>
                                    duree != null ? `${duree} jour${duree > 1 ? "s" : ""}` : "-",
                            },
                        ]}
                        data={formations}
                        onRefresh={fetchFormations}
                        isLoading={isLoading}
                        onView={(row) => {
                            console.log("Row to view:", row);
                        }}
                    />
                </div>
            </div>
        </div>
    );
}