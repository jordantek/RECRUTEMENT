import { useEffect, useState } from "react";
import { EmployeeType } from "@/types/employee/EmployeeType.ts";
import { useAuth } from "@/lib/auth.ts";
import apiRoutes from "@/api/apiRoutes.ts";
import apiService from "@/api/apiService.ts";
import { DynamicTable3 } from "@/components/tables/dynamic-table-3.tsx";
import { AbsenceType } from "@/types/AbsenceType.ts";
import { Loader2 } from "lucide-react";

interface EmployeeAbsencesProps {
    employe: EmployeeType;
}

export default function EmployeAbsencesTab({ employe }: EmployeeAbsencesProps) {
    const { user } = useAuth();
    const [absences, setAbsences] = useState<AbsenceType[]>([]);
    const [loading, setLoading] = useState(false);
    const [absenceTypes, setAbsenceTypes] = useState<{label: string, value: string}[]>([]);

    const fetchAbsences = async () => {
        try {
            setLoading(true);
            const response = await apiService.get(
                {
                    url: `${apiRoutes.admin.app.employee.absences.list_by_employee}/${employe.id}`,
                },
                {
                    userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                    hasNoSuccessModal: true,
                }
            );
            setAbsences(response.data);
        } catch (error) {
            if (error instanceof Error) {
                apiService.handleError(error.message, { hasNoFailureModal: false });
            }
        } finally {
            setLoading(false);
        }
    };

    const fetchAbsenceTypes = async () => {
        try {
            const response = await apiService.get(
                {
                    url: apiRoutes.admin.app.employee.absences.type,
                },
                {
                    userToken: `${user?.type} ${user?.token}`,
                    hasNoSuccessModal: true,
                }
            );

            if (response.data) {
                setAbsenceTypes(response.data.map((type: any) => ({
                    label: type.libelle,
                    value: type.id.toString(),
                })));
            }
        } catch (error) {
            console.error("Error fetching absence types:", error);
        }
    };

    useEffect(() => {
        fetchAbsenceTypes();
        fetchAbsences();
    }, [employe]);

    return (
        <div>
            {/*  */}
            <div className="flex items-center justify-between px-4 mb-4">
                <div className="text-left">
                    <h2 className="text-lg font-semibold">Historique des absences</h2>
                    <p className="text-xs text-muted-foreground">
                        Absences de {employe.prenom} {employe.nom}
                    </p>
                </div>
            </div>

            {/* */}
            <div className="px-4">
                <div className="m-auto w-fullgap-2">
                    {loading ? (
                        <div className="flex flex-col items-center justify-center h-32 gap-2">
                            <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
                            <span className="text-sm text-muted-foreground">
                                Chargement des absences...
                            </span>
                        </div>
                    ) : (
                        <DynamicTable3<AbsenceType>
                            columns={[
                                { 
                                    key: "typeAbsenceId",
                                    label: "Type",
                                    render: (typeId) => {
                                        const type = absenceTypes.find(t => t.value === typeId?.toString());
                                        return type?.label || 'N/A';
                                    }
                                },
                                { 
                                    key: "dateDebut", 
                                    label: "Date début",
                                    render: (date) => new Date(date).toLocaleDateString('fr-FR')
                                },
                                { 
                                    key: "dateFin", 
                                    label: "Date fin",
                                    render: (date) => new Date(date).toLocaleDateString('fr-FR')
                                },
                                { 
                                    key: "modeJouissance", 
                                    label: "Mode",
                                    render: (mode) => {
                                        switch(mode) {
                                            case "NUMERAIRE": return "Numéraire";
                                            case "REEL": return "Réel";
                                            case "DIFFERE": return "Différé";
                                            case "EPARGNE": return "Épargne";
                                            default: return mode;
                                        }
                                    }
                                },
                                {
                                    key: "status",
                                    label: "Statut",
                                    render: (status) => (
                                        <span className={`
                                            inline-flex items-center px-2 py-0.5 rounded text-xs font-medium
                                            ${status === "EN_ATTENTE" ? 'bg-yellow-100 text-yellow-800' :
                                              status === "REFUSEE" ? 'bg-red-100 text-red-800' :
                                              'bg-green-100 text-green-800'}
                                        `}>
                                            {status === "EN_ATTENTE" ? 'En attente' :
                                             status === "REFUSEE" ? 'Refusée' : 'Approuvée'}
                                        </span>
                                    ),
                                },
                            ]}
                            data={absences}
                            onRefresh={fetchAbsences}
                        />
                    )}
                </div>
            </div>
        </div>
    );
}