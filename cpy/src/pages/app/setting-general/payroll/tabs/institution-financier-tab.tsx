import {Button} from "@/components/ui/button.tsx";
import {Gavel, Plus} from "lucide-react";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {z} from "zod";
import apiService from "@/api/apiService.ts";
import apiRoutes from "@/api/apiRoutes.ts";
import {useEffect, useState} from "react";
import FormModal from "@/components/useful/form-modal.tsx";
import {Icon} from "@tabler/icons-react";
import {useAuth} from "@/lib/auth.ts";
import useCompanyStore from "@/contexts/CompanyContext.ts";
import {DynamicTable3} from "@/components/tables/dynamic-table-3.tsx";
import DeleteModal from "@/components/useful/delete-modal.tsx";

type InstitutionType ={
    id:number,
    name:string,
}
export default function InstitutionFinancierTab() {
    const { user, logout } = useAuth();
    const { selectedCompany } = useCompanyStore();
    const [isOpenForm, setIsOpenForm] = useState(false);
    const [isOpenDelete, setIsOpenDelete] = useState(false);
    const [institutions, setInstitutions] = useState<InstitutionType[]>([]);
    const [selectedInstitus, setSelectedInstitus] = useState<InstitutionType | null>(null);
    const [loadingSubmit, setLoadingSubmit] = useState(false);
    const [loadingPage, setLoadingPage] = useState(false);
    const institutFields = [
        {
            tag: "name",
            label: "Nom",
            input_type: "text",
            size: "col-span-12",
            required: true
        },
    ];

     const institutionSchema = z.object({
        name: z.string({
            required_error: "le nom est obligatoire",
            invalid_type_error: "Le champ doit contenir uniquement du texte"
        }),
    });


   type InstitutionFormValues = z.infer<typeof institutionSchema>;

    const formInstitution= useForm<InstitutionFormValues>({
        resolver: zodResolver(institutionSchema),
        defaultValues: {},
        shouldFocusError: true,
    });

    const resetForm= formInstitution.reset;

    const handleOnCreate = async (data: InstitutionFormValues) => {
        setLoadingSubmit(true);
        try {
                await apiService.post(
                    {
                        url: apiRoutes.admin.app.institutionFinancier.create,
                        body: JSON.stringify(data),
                        headers: { "Content-Type": "application/json" },
                    },
                    {
                        userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                        hasNoSuccessModal: false,
                        onTokenExpired: logout,
                    }
                );
            resetForm();
            setIsOpenForm(false);

        } catch (error) {
            if (error instanceof Error) {
                apiService.handleError(error.message, {hasNoFailureModal: false});
            } else {
                console.log("Une erreur inconnue est survenue");
            }
        } finally {
            setLoadingSubmit(false);
        }
    };

    const handleOnEdit = async (data: InstitutionFormValues) => {
        setLoadingSubmit(true);
        try {
            await apiService.put(
                {
                    url:`${apiRoutes.admin.app.institutionFinancier.update}${selectedInstitus?.id}`,
                    body: JSON.stringify(data),
                    headers: { "Content-Type": "application/json" },
                },
                {
                    userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                    hasNoSuccessModal: false,
                    onTokenExpired: logout,
                }
            );
            resetForm();
            setInstitutions([])
            OnFetchInstitution()
            setIsOpenForm(false);


        } catch (error) {
            if (error instanceof Error) {
                apiService.handleError(error.message, {hasNoFailureModal: false});
            } else {
                console.log("Une erreur inconnue est survenue");
            }
        } finally {
            setLoadingSubmit(false);
        }
    };

    const handleOnDelete = async () => {
        if (selectedInstitus) {
            try {
                setLoadingSubmit(true);
                await apiService.remove(
                    {
                        url: `${apiRoutes.admin.app.institutionFinancier.delete}${selectedInstitus.id}`,
                    },
                    {
                        userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                        hasNoSuccessModal: false,
                        onTokenExpired: logout,
                    }
                );
                setInstitutions([])
                OnFetchInstitution()
                setIsOpenDelete(false);
            } catch (error) {
                if (error instanceof Error) {
                    apiService.handleError(error.message, { hasNoFailureModal: false });
                }
            } finally {
                setLoadingSubmit(false);
            }
        }
    }

    const OnFetchInstitution = async () => {
        if (!selectedCompany?.id) return;

        setLoadingPage(true);
        try {
            const response = await apiService.get(
                {
                    url: `${apiRoutes.admin.app.institutionFinancier.list}`,
                },
                {
                    userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                    hasNoSuccessModal: true,
                    onTokenExpired: logout,
                }
            );

            if (response.data) {
                const institus: InstitutionType[] = response.data;
                setInstitutions(institus);
            }
        } catch (error) {
            console.error("Error fetching sanctions:", error);
        } finally {
            setLoadingPage(false);
        }
    };

    const handleEdit = (row: { id: number }) => {
        const inst = institutions.find(s => s.id === row.id);
        if (inst) {
            setSelectedInstitus(inst);
            formInstitution.reset({
                name: inst.name
            });

            setIsOpenForm(true);
        }
    };

    const handleDelete = (row: { id: number }) => {
        const inst = institutions.find(s => s.id === row.id);
        if (inst) {
            setSelectedInstitus(inst);
            setIsOpenDelete(true);
        }
    };

    useEffect(() => {
        OnFetchInstitution().then(r => console.log(r))
    }, [selectedCompany?.id]);
    return (
        <>
            <div className="flex items-center justify-between mb-4">
                <h2 className="text-lg font-semibold">Institutions Financières</h2>

                <Button
                    size={"sm"}
                    className="flex items-center gap-2 rounded-md bg-primary text-primary-foreground hover:bg-primary/80"
                    onClick={() => {

                        setIsOpenForm(true);
                    }}
                >
                    <Plus className="h-4 w-4 mr-2"/>
                    Ajouter
                </Button>

            </div>

            <div className={"px-64"}>
                <DynamicTable3
                    columns={[
                        {
                            key: "name",
                            label: "Noms",
                        },
                    ]}
                    data={institutions}
                    onEdit={handleEdit}
                    onAdd={() => setIsOpenForm(true)}
                    onDelete={handleDelete}
                    isLoading={loadingPage}

                />
            </div>

            <DeleteModal
                title={"⚠️ Etes-vous sur de vouloir supprimer ?"}
                description={`Cette action est irréversible. En cliquant sur supprimer, vous supprimerez l'institution: ${selectedInstitus?.name}.`}
                isOpen={isOpenDelete}
                isetIsOpen={setIsOpenDelete}
                isDeleteLoading={loadingSubmit}
                onDelete={handleOnDelete}
                onCancel={()=>{
                    setSelectedInstitus(null)
                    setIsOpenDelete(false)
                }}
            />

            <FormModal
                icon={Gavel as Icon}
                title={selectedInstitus? "Modifier" : "Ajouter une institution financière"}
                description={selectedInstitus ? "Modifiez les informations de la sanction" : "Remplissez les informations ci-dessous pour ajouter une institution financière."}
                isOpen={isOpenForm}
                setIsOpen={setIsOpenForm}
                form={formInstitution}
                fields={institutFields}
                onSubmit={selectedInstitus?handleOnEdit:handleOnCreate}
                onSubmitSuccess={()=>{
                    setInstitutions([])
                    OnFetchInstitution()
                }}
                isSubmitLoading={loadingSubmit}
                onClose={() => {
                    setSelectedInstitus(null);
                    setLoadingSubmit(false);
                    resetForm();
                }}
            />
        </>
    );
}
