import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { AddChildreModal } from "@/components/layout/employee/employee-childre/add-childre-modal";
import { EmployeeType, EnfantType } from "@/types/employee/EmployeeType";
import { useAuth } from "@/lib/auth";
import apiRoutes from "@/api/apiRoutes";
import apiService from "@/api/apiService";
import { Skeleton } from "@/components/ui/skeleton.tsx";
import {DynamicTable3} from "@/components/tables/dynamic-table-3.tsx";
import {Baby} from "lucide-react";
import {EditChildreModal} from "@/components/layout/employee/employee-childre/edit-childre-modal.tsx";
import DeleteModal from "@/components/useful/delete-modal.tsx";

interface EmployeeChildrenManagementProps {
    employe: EmployeeType;
}

export default function EmployeChildrenTab({ employe }: EmployeeChildrenManagementProps) {
    const { user,logout } = useAuth();
    const [isOpenAddChildrenModal, setIsOpenAddChildrenModal] = useState(false);
    const [isOpenEditChildrenModal, setIsOpenEditChildrenModal] = useState(false);
    const [isOpenDeleteChildrenModal, setIsOpenDeleteChildrenModal] = useState(false);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [enfants, setEnfants] = useState<EnfantType[]>([]);
    const [enfantsAdapted, setEnfantsAdapted] = useState<any[]>([]);
    const [loadingEnfants, setLoadingEnfants] = useState(false);
    const [selectedEnfant, setSelectedEnfant] = useState<EnfantType | null>(null);

    // Sécurité : ne rien afficher si employe ou employe.id n'est pas défini
    if (!employe?.id) return null;

    const fetchEnfants = async () => {
        if (!employe?.id) return;
        try {
            setLoadingEnfants(true);
            const response = await apiService.get(
                {
                    url: `${apiRoutes.admin.app.employee.children.list}${employe.id}`,
                },
                {
                    userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                    hasNoSuccessModal: true,
                }
            );

            const enfantsData: EnfantType[] = response.data;
            setEnfants(enfantsData);

            const adapted = enfantsData.map((enfant) => ({
                id: enfant.id,
                nomPrenom: `${enfant.nom} ${enfant.prenom}`,
                sexe: enfant.sexe,
                dateN: enfant.dateNaissance,
                lieuN: enfant.lieuNaissance,
            }));
            setEnfantsAdapted(adapted);

        } catch (error) {
            console.error("Erreur de chargement des enfants", error);
        } finally {
            setLoadingEnfants(false);
        }
    };

    const handleEdit = (row: { id: number }) => {
        const enfant = enfants.find(e => e.id === row.id);
        if (enfant) {
            setSelectedEnfant(enfant);
            setIsOpenEditChildrenModal(true);
        }
    };

    const handleDelete = (row: { id: number }) => {
        const enfant = enfants.find(e => e.id === row.id);
        if (enfant) {
           setSelectedEnfant(enfant);
           setIsOpenDeleteChildrenModal(true);
        }
    };

    const handleOnDelete = async () => {
        if (selectedEnfant) {
            try {
                setIsSubmitting(true);
                await apiService.remove(
                    {
                        url: `${apiRoutes.admin.app.employee.children.delete}/${selectedEnfant.id}`,
                    },
                    {
                        userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                        hasNoSuccessModal: false,
                        onTokenExpired: logout,
                    }
                );
                fetchEnfants();
                setIsOpenDeleteChildrenModal(false);
            } catch (error) {
                if (error instanceof Error) {
                    apiService.handleError(error.message, { hasNoFailureModal: false });
                }
            } finally {
                setIsSubmitting(false);
            }
        }
    }

    useEffect(() => {
        if (!isOpenAddChildrenModal) {
            fetchEnfants();
        }
    }, [isOpenAddChildrenModal]);

    useEffect(() => {
        setEnfants([]);
        setEnfantsAdapted([]);
        fetchEnfants();
    }, [employe]);

    return (
        <div className="space-y-3 gap-4">
            <DeleteModal
                title={"⚠️ Etes-vous sur de vouloir supprimer ?"}
                description={`Cette action est irréversible. En cliquant sur supprimer, vous supprimerez l'enfant ${selectedEnfant?.nom} ${selectedEnfant?.prenom}.`}
                isOpen={isOpenDeleteChildrenModal}
                isetIsOpen={setIsOpenDeleteChildrenModal}
                isDeleteLoading={isSubmitting}
                onDelete={handleOnDelete}
                onCancel={()=>{
                    setSelectedEnfant(null)
                    setIsOpenDeleteChildrenModal(false)
                }}
            />
            <AddChildreModal
                isOpen={isOpenAddChildrenModal}
                setIsOpen={setIsOpenAddChildrenModal}
                employeId={employe.id}
            />

            <EditChildreModal
                isOpen={isOpenEditChildrenModal}
                setIsOpen={setIsOpenEditChildrenModal}
                enfant={selectedEnfant}
            />

            <div className="flex items-center justify-between bg-background px-4 ">
                <div>
                    <h2 className="text-lg font-semibold">Liste des enfants de l'employé</h2>
                    <p className="text-xs text-muted-foreground">
                        Vous pouvez ajouter ou supprimer les enfants de cet employé.
                    </p>
                </div>
                <div className="mt-2">
                    <Button  size={"sm"} onClick={() => setIsOpenAddChildrenModal(true)}>
                            <Baby size={24} className={"me-1"} />
                        Ajouter un enfant
                    </Button>
                </div>
            </div>
            <div className="bg-background overflow-hidden rounded-md  px-4">
                {loadingEnfants ? (
                    <div className="p-4 space-y-3">
                        {[...Array(3)].map((_, i) => (
                            <div key={i} className="flex gap-4">
                                <Skeleton className="h-4 w-[30%]" />
                                <Skeleton className="h-4 w-[20%]" />
                                <Skeleton className="h-4 w-[30%]" />
                            </div>
                        ))}
                    </div>
                ) : (
                    <DynamicTable3
                        columns={[
                            { key: "nomPrenom", label: "Nom & Prénom" },
                            { key: "sexe", label: "Sexe" },
                            { key: "dateN", label: "Date de naissance" },
                            {key:"lieuN",label:"Lieu de naissance"},
                        ]}
                        data={enfantsAdapted}
                        onEdit={handleEdit}
                        onDelete={handleDelete}
                    />

                )}
            </div>
        </div>
    );
}